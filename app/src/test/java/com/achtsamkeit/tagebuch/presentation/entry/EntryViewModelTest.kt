package com.achtsamkeit.tagebuch.presentation.entry

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.achtsamkeit.tagebuch.domain.model.GuidedQuestion
import com.achtsamkeit.tagebuch.domain.model.MoodLevel
import com.achtsamkeit.tagebuch.domain.usecase.GetEntryByIdUseCase
import com.achtsamkeit.tagebuch.domain.usecase.GetSelectedQuestionsUseCase
import com.achtsamkeit.tagebuch.domain.usecase.SaveEntryUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EntryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val getSelectedQuestionsUseCase: GetSelectedQuestionsUseCase = mockk()
    private val saveEntryUseCase: SaveEntryUseCase = mockk()
    private val getEntryByIdUseCase: GetEntryByIdUseCase = mockk()
    private lateinit var viewModel: EntryViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { getSelectedQuestionsUseCase() } returns flowOf(
            listOf(GuidedQuestion(id = 1, question = "Frage 1", isSelected = true))
        )

        viewModel = EntryViewModel(
            getSelectedQuestionsUseCase = getSelectedQuestionsUseCase,
            saveEntryUseCase = saveEntryUseCase,
            getEntryByIdUseCase = getEntryByIdUseCase,
            savedStateHandle = SavedStateHandle()
        )
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads questions`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.guidedAnswers.size)
            assertEquals("Frage 1", state.guidedAnswers[0].question)
        }
    }

    @Test
    fun `onMoodChanged updates state`() = runTest {
        viewModel.onMoodChanged(MoodLevel.SEHR_GUT)
        assertEquals(MoodLevel.SEHR_GUT, viewModel.uiState.value.moodLevel)
    }

    @Test
    fun `saveEntry calls saveEntryUseCase`() = runTest {
        coEvery { saveEntryUseCase(any()) } returns 1L

        viewModel.onFreeTextChanged("Mein Tag war toll.")
        viewModel.saveEntry()

        coVerify { saveEntryUseCase(any()) }
        assertEquals(true, viewModel.uiState.value.isSaved)
    }
}
