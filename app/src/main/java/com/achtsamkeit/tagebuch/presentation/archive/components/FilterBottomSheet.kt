package com.achtsamkeit.tagebuch.presentation.archive.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.achtsamkeit.tagebuch.R
import com.achtsamkeit.tagebuch.domain.model.MoodLevel
import com.achtsamkeit.tagebuch.presentation.archive.ArchiveUiState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheetContent(
    uiState: ArchiveUiState,
    onMoodToggle: (Int) -> Unit,
    onDateFromChange: (LocalDate?) -> Unit,
    onDateToChange: (LocalDate?) -> Unit,
    onLabelToggle: (String) -> Unit,
    onReset: () -> Unit
) {
    var showDatePickerFrom by remember { mutableStateOf(false) }
    var showDatePickerTo by remember { mutableStateOf(false) }

    if (showDatePickerFrom) {
        MyDatePickerDialog(
            initialDate = uiState.filterState.dateFrom ?: LocalDate.now(),
            onDateSelected = {
                onDateFromChange(it)
                showDatePickerFrom = false
            },
            onDismiss = { showDatePickerFrom = false }
        )
    }

    if (showDatePickerTo) {
        MyDatePickerDialog(
            initialDate = uiState.filterState.dateTo ?: LocalDate.now(),
            onDateSelected = {
                onDateToChange(it)
                showDatePickerTo = false
            },
            onDismiss = { showDatePickerTo = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.archive_filter_title), style = MaterialTheme.typography.headlineSmall)
            if (uiState.filterState.isActive) {
                TextButton(onClick = onReset) {
                    Text(stringResource(R.string.archive_filter_reset))
                }
            }
        }

        // Mood Filter
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.archive_filter_mood_title), style = MaterialTheme.typography.titleMedium)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MoodLevel.entries.forEach { mood ->
                    FilterChip(
                        selected = mood.score in uiState.filterState.selectedMoodScores,
                        onClick = { onMoodToggle(mood.score) },
                        label = { Text("${mood.emoji} ${mood.label}") }
                    )
                }
            }
        }

        // Date Filter
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            Text(stringResource(R.string.archive_filter_date_title), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedCard(
                    onClick = { showDatePickerFrom = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = uiState.filterState.dateFrom?.format(dateFormatter) ?: stringResource(R.string.archive_filter_date_from_placeholder),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                OutlinedCard(
                    onClick = { showDatePickerTo = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = uiState.filterState.dateTo?.format(dateFormatter) ?: stringResource(R.string.archive_filter_date_to_placeholder),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Label Filter
        if (uiState.availableLabels.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.archive_filter_labels_title), style = MaterialTheme.typography.titleMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    uiState.availableLabels.forEach { label ->
                        FilterChip(
                            selected = label in uiState.filterState.selectedLabels,
                            onClick = { onLabelToggle(label) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    onDateSelected(date)
                }
            }) {
                Text(stringResource(R.string.archive_date_picker_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.archive_date_picker_cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
