package de.westnordost.streetcomplete.data.osm.edits.upload

import com.russhwolf.settings.ObservableSettings
import de.westnordost.streetcomplete.data.ConflictException
import de.westnordost.streetcomplete.BuildConfig
import de.westnordost.streetcomplete.Prefs
import de.westnordost.streetcomplete.data.download.Downloader
import de.westnordost.streetcomplete.data.osm.edits.ElementEdit
import de.westnordost.streetcomplete.data.osm.edits.ElementEditsController
import de.westnordost.streetcomplete.data.osm.edits.ElementIdProvider
import de.westnordost.streetcomplete.data.osm.mapdata.Element
import de.westnordost.streetcomplete.data.osm.mapdata.ElementKey
import de.westnordost.streetcomplete.data.osm.mapdata.ElementType
import de.westnordost.streetcomplete.data.osm.mapdata.MapData
import de.westnordost.streetcomplete.data.osm.mapdata.MapDataApi
import de.westnordost.streetcomplete.data.osm.mapdata.MapDataController
import de.westnordost.streetcomplete.data.osm.mapdata.MapDataUpdates
import de.westnordost.streetcomplete.data.osm.mapdata.MutableMapData
import de.westnordost.streetcomplete.data.osmnotes.edits.NoteEditsController
import de.westnordost.streetcomplete.data.externalsource.ExternalSourceQuestController
import de.westnordost.streetcomplete.data.externalsource.ExternalSourceQuestType
import de.westnordost.streetcomplete.data.osm.edits.IsRevertAction
import de.westnordost.streetcomplete.data.upload.OnUploadedChangeListener
import de.westnordost.streetcomplete.data.upload.Uploader
import de.westnordost.streetcomplete.data.user.UserLoginController
import de.westnordost.streetcomplete.data.user.statistics.StatisticsController
import de.westnordost.streetcomplete.util.logs.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class ElementEditsUploader(
    private val elementEditsController: ElementEditsController,
    private val noteEditsController: NoteEditsController,
    private val mapDataController: MapDataController,
    private val singleUploader: ElementEditUploader,
    private val mapDataApi: MapDataApi,
    private val statisticsController: StatisticsController,
    private val downloader: Downloader,
    private val externalSourceQuestController: ExternalSourceQuestController,
    private val prefs: ObservableSettings,
) {
    var uploadedChangeListener: OnUploadedChangeListener? = null

    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + CoroutineName("ElementEditsUploader"))

    suspend fun upload(uploader: Uploader) = mutex.withLock { withContext(Dispatchers.IO) {
        while (true) {
            val edit = elementEditsController.getOldestUnsynced() ?: break
            val getIdProvider: () -> ElementIdProvider = { elementEditsController.getIdProvider(edit.id) }
            if (downloader.isDownloadInProgress) {
                // cancel upload, and re-start uploading a second later
                // then download will already be running
                scope.launch {
                    delay(1000)
                    try {
                        uploader.upload()
                    } catch (e: Exception) {
                        Log.i(TAG, "exception when continuing interrupted upload: $e")
                        throw CancellationException() // not caught by UploadWorker.doWork, but we don't want
                    }
                }
                throw CancellationException() // don't simply break, because otherwise upload will continue with notes
            }
            /* the sync of local change -> API and its response should not be cancellable because
             * otherwise an inconsistency in the data would occur. E.g. no "star" for an uploaded
             * change, a change could be uploaded twice etc */
            withContext(scope.coroutineContext) { uploadEdit(edit, getIdProvider) }
            if (BuildConfig.DEBUG && !UserLoginController.loggedIn)
                break // slow uploading is much better to read in logs
        }
    } }

    private suspend fun uploadEdit(edit: ElementEdit, getIdProvider: () -> ElementIdProvider) {
        val editActionClassName = edit.action::class.simpleName!!

        try {
            if (edit.type is ExternalSourceQuestType && !externalSourceQuestController.onUpload(edit))
                throw(ConflictException())
            val updates = singleUploader.upload(edit, getIdProvider)

            Log.d(TAG, "Uploaded a $editActionClassName for ${edit.action.elementKeys}")
            uploadedChangeListener?.onUploaded(edit.type.name, edit.position)

            elementEditsController.markSynced(edit, updates)
            mapDataController.updateAll(updates)
            noteEditsController.updateElementIds(updates.idUpdates)

            if (prefs.getBoolean(Prefs.UPDATE_LOCAL_STATISTICS, true)) {
                if (edit.action is IsRevertAction) {
                    statisticsController.subtractOne(edit.type.name, edit.position)
                } else {
                    statisticsController.addOne(edit.type.name, edit.position)
                }
            }
        } catch (e: ConflictException) {
            Log.d(TAG, "Dropped a $editActionClassName for ${edit.action.elementKeys}: ${e.message}")
            uploadedChangeListener?.onDiscarded(edit.type.name, edit.position)

            externalSourceQuestController.onSyncEditFailed(edit)
            elementEditsController.markSyncFailed(edit)

            /* fetching the current version of the element(s) edited on conflict and persisting
               them is not really optional, as when the edit has been deleted due to the conflict,
               the quests etc. would otherwise just be displayed again as if the user didn't solve
               them */
            val updated = mutableListOf<Element>()
            val deleted = mutableListOf<ElementKey>()

            for (elementKey in edit.action.elementKeys) {
                val mapData = fetchElementComplete(elementKey.type, elementKey.id)
                if (mapData != null) {
                    updated.addAll(mapData)
                } else {
                    deleted.add(elementKey)
                }
            }
            if (updated.isNotEmpty() || deleted.isNotEmpty()) {
                mapDataController.updateAll(MapDataUpdates(updated = updated, deleted = deleted))
            }
        }
    }

    private suspend fun fetchElementComplete(elementType: ElementType, elementId: Long): MapData? =
        withContext(Dispatchers.IO) {
            when (elementType) {
                ElementType.NODE -> mapDataApi.getNode(elementId)?.let { MutableMapData(listOf(it)) }
                ElementType.WAY -> mapDataApi.getWayComplete(elementId)
                ElementType.RELATION -> mapDataApi.getRelationComplete(elementId)
            }
        }

    companion object {
        private const val TAG = "ElementEditsUploader"
    }
}
