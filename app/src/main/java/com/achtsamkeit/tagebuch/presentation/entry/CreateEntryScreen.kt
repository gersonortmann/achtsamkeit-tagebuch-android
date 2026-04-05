package com.achtsamkeit.tagebuch.presentation.entry

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.achtsamkeit.tagebuch.domain.model.MoodLevel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEntryScreen(
    navController: NavController,
    viewModel: EntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.guidedAnswers.any { it.answer.isNotBlank() } || uiState.freeText.isNotBlank()) "Eintrag bearbeiten" else "Neuer Eintrag") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        IconButton(onClick = { viewModel.saveEntry() }) {
                            Icon(Icons.Default.Check, contentDescription = "Speichern")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Datum Auswahl
            DatePickerSection(
                selectedDate = uiState.entryDate,
                onDateSelected = viewModel::onDateChanged
            )

            // Stimmungsslider
            MoodSection(
                selectedMood = uiState.moodLevel,
                onMoodChanged = viewModel::onMoodChanged
            )

            // Geführte Fragen
            uiState.guidedAnswers.forEach { guidedAnswer ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Reflexion",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = guidedAnswer.question,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = guidedAnswer.answer,
                            onValueChange = { viewModel.onGuidedAnswerChanged(guidedAnswer.question, it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Deine Antwort...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }

            // Dankbarkeit
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Wofür bist du heute dankbar?",
                    style = MaterialTheme.typography.titleMedium
                )
                uiState.gratitudeItems.forEachIndexed { index, item ->
                    OutlinedTextField(
                        value = item,
                        onValueChange = { viewModel.onGratitudeItemChanged(index, it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("${index + 1}. Dankbarkeitspunkt") },
                        leadingIcon = { Text("${index + 1}") }
                    )
                }
            }

            // Freitext
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Gedanken & Notizen",
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    value = uiState.freeText,
                    onValueChange = viewModel::onFreeTextChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp),
                    placeholder = { Text("Was beschäftigt dich heute?") }
                )
            }

            // Tags Sektion
            TagSection(
                tags = uiState.tags,
                onTagAdded = viewModel::onTagAdded,
                onTagRemoved = viewModel::onTagRemoved
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSection(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val showDatePicker = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        )
                    }
                    showDatePicker.value = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker.value = false }) {
                    Text("Abbrechen")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Datum:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        TextButton(
            onClick = { showDatePicker.value = true },
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("dd. MMMM yyyy")),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSection(
    tags: List<String>,
    onTagAdded: (String) -> Unit,
    onTagRemoved: (String) -> Unit
) {
    var tagInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Tags",
            style = MaterialTheme.typography.titleMedium
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                InputChip(
                    selected = true,
                    onClick = { onTagRemoved(tag) },
                    label = { Text(tag) },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Entfernen",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }

        OutlinedTextField(
            value = tagInput,
            onValueChange = { tagInput = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Neuen Tag hinzufügen...") },
            trailingIcon = {
                IconButton(onClick = {
                    if (tagInput.isNotBlank()) {
                        onTagAdded(tagInput.trim())
                        tagInput = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Hinzufügen")
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (tagInput.isNotBlank()) {
                    onTagAdded(tagInput.trim())
                    tagInput = ""
                }
                focusManager.clearFocus()
            })
        )
    }
}

@Composable
fun MoodSection(
    selectedMood: MoodLevel,
    onMoodChanged: (MoodLevel) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Wie fühlst du dich heute?",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = selectedMood.emoji,
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = selectedMood.label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Slider(
            value = selectedMood.score.toFloat(),
            onValueChange = { onMoodChanged(MoodLevel.fromScore(it.toInt())) },
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
