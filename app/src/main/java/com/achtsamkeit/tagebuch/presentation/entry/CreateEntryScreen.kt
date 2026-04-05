package com.achtsamkeit.tagebuch.presentation.entry

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
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
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreateEntryScreen(
    navController: NavController,
    viewModel: EntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    var showSaveErrorDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }

    // Exit Confirmation Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Bearbeitung beenden?") },
            text = { Text("Deine Änderungen wurden noch nicht gespeichert. Möchtest du sie wirklich verwerfen?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    navController.popBackStack()
                }) {
                    Text("Zurück ohne Speichern")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Weiterschreiben")
                }
            }
        )
    }

    // Save Error Dialog
    if (showSaveErrorDialog) {
        AlertDialog(
            onDismissRequest = { showSaveErrorDialog = false },
            title = { Text("Eintrag leer") },
            text = { Text("Bitte schreibe erst etwas in dein Tagebuch, bevor du speicherst.") },
            confirmButton = {
                TextButton(onClick = { showSaveErrorDialog = false }) {
                    Text("Weiterschreiben")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSaveErrorDialog = false
                    navController.popBackStack()
                }) {
                    Text("Ohne Speichern zurück")
                }
            }
        )
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error == "Bitte schreibe etwas in dein Tagebuch.") {
            showSaveErrorDialog = true
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.isEditMode) "Eintrag bearbeiten" else "Neuer Eintrag")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val hasInput = uiState.freeText.isNotBlank() ||
                                uiState.guidedAnswers.any { it.answer.isNotBlank() } ||
                                uiState.gratitudeItems.any { it.isNotBlank() } ||
                                uiState.labels.isNotEmpty()
                        val moodChanged = uiState.moodLevel != MoodLevel.NEUTRAL
                        
                        if (uiState.isEditMode || hasInput || moodChanged) {
                            showExitDialog = true
                        } else {
                            navController.popBackStack()
                        }
                    }) {
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
                .imePadding()
                .verticalScroll(scrollState)
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
                val bringIntoViewReq = remember { BringIntoViewRequester() }
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
                            onValueChange = { 
                                viewModel.onGuidedAnswerChanged(guidedAnswer.question, it)
                                scope.launch { bringIntoViewReq.bringIntoView() }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(bringIntoViewReq),
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
            val freeTextBringIntoViewReq = remember { BringIntoViewRequester() }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Gedanken & Notizen",
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    value = uiState.freeText,
                    onValueChange = { 
                        viewModel.onFreeTextChanged(it)
                        scope.launch { freeTextBringIntoViewReq.bringIntoView() }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp)
                        .bringIntoViewRequester(freeTextBringIntoViewReq),
                    placeholder = { Text("Was beschäftigt dich heute?") }
                )
            }

            // Labels Sektion
            LabelSection(
                labels = uiState.labels,
                onLabelAdded = viewModel::onLabelAdded,
                onLabelRemoved = viewModel::onLabelRemoved
            )

            // Zusätzlicher Platz am Ende
            Spacer(modifier = Modifier.height(32.dp))
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
fun LabelSection(
    labels: List<String>,
    onLabelAdded: (String) -> Unit,
    onLabelRemoved: (String) -> Unit
) {
    var labelInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Labels",
            style = MaterialTheme.typography.titleMedium
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            labels.forEach { label ->
                InputChip(
                    selected = true,
                    onClick = { onLabelRemoved(label) },
                    label = { Text(label) },
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
            value = labelInput,
            onValueChange = { labelInput = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Neues Label hinzufügen...") },
            trailingIcon = {
                IconButton(onClick = {
                    if (labelInput.isNotBlank()) {
                        onLabelAdded(labelInput.trim())
                        labelInput = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Hinzufügen")
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (labelInput.isNotBlank()) {
                    onLabelAdded(labelInput.trim())
                    labelInput = ""
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
