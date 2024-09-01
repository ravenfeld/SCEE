package de.westnordost.streetcomplete.screens.user.edits

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.westnordost.streetcomplete.ui.theme.GrassGreen
import de.westnordost.streetcomplete.ui.theme.LeafGreen

/** Simple bar chart of solved quests by quest type */
@Composable
fun EditTypeStatisticsColumn(
    statistics: List<CompleteEditTypeStatistics>,
    modifier: Modifier = Modifier,
) {
    var showInfo by remember { mutableStateOf<CompleteEditTypeStatistics?>(null) }

    // list is sorted by largest count descending
    val maxCount = statistics.firstOrNull()?.count ?: 0
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 16.dp)
    ) {
        items(
            items = statistics,
            key = { it.type.name }
        ) { item ->
            BarChartRow(
                title = {
                    Image(
                        painter = painterResource(item.type.icon),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                },
                count = item.count,
                countNew = item.countCurrentWeek,
                maxCount = maxCount,
                modifier = Modifier
                    .clickable { showInfo = item }
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                color = GrassGreen,
                colorNew = LeafGreen
            )
        }
    }
    showInfo?.let {
        EditTypeDialog(
            editType = it.type,
            onDismissRequest = { showInfo = null }
        )
    }
}
