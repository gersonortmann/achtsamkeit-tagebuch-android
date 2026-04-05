package com.achtsamkeit.tagebuch.presentation.archive.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.achtsamkeit.tagebuch.R
import com.achtsamkeit.tagebuch.domain.model.JournalEntry
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArchiveEntryItem(
    entry: JournalEntry,
    showLabelsInline: Boolean = false,
    onClick: () -> Unit,
    onLabelClick: (String) -> Unit = {}
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy")

    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(entry.moodEmoji, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = entry.date.format(dateFormatter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        supportingContent = {
            Column {
                if (entry.freeText.isNotBlank()) {
                    Text(
                        text = entry.freeText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (showLabelsInline && entry.labels.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        entry.labels.forEach { label ->
                            SuggestionChip(
                                onClick = { onLabelClick(label) },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            }
        },
        trailingContent = {
            if (!showLabelsInline && entry.labels.isNotEmpty()) {
                val labelText = if (entry.labels.size == 1) {
                    stringResource(R.string.archive_label_singular)
                } else {
                    stringResource(R.string.archive_label_plural, entry.labels.size)
                }
                Text(
                    text = labelText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}
