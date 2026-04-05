package com.achtsamkeit.tagebuch.presentation.archive

import com.achtsamkeit.tagebuch.domain.model.JournalEntry
import com.achtsamkeit.tagebuch.domain.usecase.GetEntriesUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ArchiveViewModelTest {

    private val getEntriesUseCase = mockk<GetEntriesUseCase>()
    private lateinit var viewModel: ArchiveViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testEntries = listOf(
        JournalEntry(
            id = 1,
            date = LocalDate.now(),
            moodScore = 5,
            moodEmoji = "😄",
            freeText = "Heute war ein toller Tag im Park",
            labels = listOf("Natur", "Sonne")
        ),
        JournalEntry(
            id = 2,
            date = LocalDate.now().minusDays(1),
            moodScore = 3,
            moodEmoji = "😐",
            freeText = "Ganz okay, viel Arbeit erledigt",
            labels = listOf("Arbeit")
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getEntriesUseCase() } returns flowOf(testEntries)
        viewModel = ArchiveViewModel(getEntriesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state shows all entries sorted by date`() = runTest {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(2, state.entries.size)
        assertEquals(1L, state.entries[0].id) // Newest first
    }

    @Test
    fun `search by free text filters entries`() = runTest {
        advanceUntilIdle()
        viewModel.onSearchQueryChange("Park")
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(1, state.entries.size)
        assertEquals("Heute war ein toller Tag im Park", state.entries[0].freeText)
    }

    @Test
    fun `search by tag filters entries`() = runTest {
        advanceUntilIdle()
        viewModel.onSearchQueryChange("Arbeit")
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(1, state.entries.size)
        assertEquals(2L, state.entries[0].id)
    }

    @Test
    fun `search is case insensitive`() = runTest {
        advanceUntilIdle()
        viewModel.onSearchQueryChange("SONNE")
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(1, state.entries.size)
        assertEquals(1L, state.entries[0].id)
    }

    @Test
    fun `empty search query shows all entries`() = runTest {
        advanceUntilIdle()
        viewModel.onSearchQueryChange("Park")
        advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.entries.size)

        viewModel.onSearchQueryChange("")
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.entries.size)
    }
}
