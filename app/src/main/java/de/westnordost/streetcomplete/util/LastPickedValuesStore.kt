package de.westnordost.streetcomplete.util

import com.russhwolf.settings.ObservableSettings
import de.westnordost.streetcomplete.Prefs

class LastPickedValuesStore<T : Any>(
    private val prefs: ObservableSettings,
    private val key: String,
    private val serialize: (T) -> String,
    private val deserialize: (String) -> T?, // null = unwanted value, see mostCommonWithin
    private val maxEntries: Int = 50
) {
    fun add(newValues: Iterable<T>) {
        // only allow up to maxCount consecutive entries
        // this reduces rare entries getting pushed out of the list by very common entries
        // e.g. 50 consecutive asphalt surface answers would remove the second most common surface
        var previous = ""
        var previousCount = 1
        val maxCount = 2
        val lastValues = (newValues.asSequence().map(serialize) + getRaw()).filter {
            if (it != previous) {
                previous = it
                previousCount = 1
                true
            } else if (previousCount <= maxCount) {
                previousCount++
                true
            } else {
                false
            }
        }
        prefs.putString(getKey(), lastValues.take(maxEntries).joinToString(","))
    }

    fun add(value: T) = add(listOf(value))

    fun get(): Sequence<T?> = getRaw().map(deserialize)

    private fun getRaw(): Sequence<String> =
        prefs.getStringOrNull(getKey())?.splitToSequence(",") ?: sequenceOf()

    private fun getKey() = Prefs.LAST_PICKED_PREFIX + key
}

/**
 * Returns the [target] most-common non-null items in the first [historyCount]
 *  items of the sequence, in their original order.
 * If there are fewer than [target] unique items, continues counting items
 *  until that many are found, or the end of the sequence is reached.
 * If the [first] most recent items are not null, they are always included,
 *  displacing the least-common of the other items if necessary.
 */
fun <T : Any> Sequence<T?>.mostCommonWithin(target: Int, historyCount: Int, first: Int): Sequence<T> {
    val counts = this.countUniqueNonNull(target, historyCount)
    val top = counts.keys.sortedByDescending { counts[it]!!.count }.take(target)
    val latest = this.take(first).filterNotNull()
    val items = (latest + top).distinct().take(target)
    return items.sortedBy { counts[it]!!.indexOfFirst }
}

private data class ItemStats(val indexOfFirst: Int, var count: Int = 0)

// Counts at least the first `minItems`, keeps going until it finds at least `target` unique values
private fun <T : Any> Sequence<T?>.countUniqueNonNull(target: Int, minItems: Int): Map<T, ItemStats> {
    val counts = mutableMapOf<T, ItemStats>()

    for (item in this.withIndex()) {
        if (item.index >= minItems && counts.size >= target) break
        item.value?.let { value ->
            counts.getOrPut(value) { ItemStats(item.index) }.count++
        }
    }

    return counts
}

fun <T> Sequence<T>.padWith(defaults: List<T>, count: Int = defaults.size) =
    (this + defaults).distinct().take(count)
