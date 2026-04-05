package com.achtsamkeit.tagebuch.presentation.entry

import androidx.lifecycle.SavedStateHandle
import com.achtsamkeit.tagebuch.domain.model.JournalEntry
import com.achtsamkeit.tagebuch.domain.usecase.DeleteEntryUseCase
import com.achtsamkeit.tagebuch.domain.usecase.GetEntryByIdUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class EntryDetailViewModelTest {

    private val getEntryByIdUseCase = mockk<GetEntryByIdUseCase>()
    private val deleteEntryUseCase = mockk<DeleteEntryUseCase>()
    private lateinit var viewModel: EntryDetailViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testEntry = JournalEntry(
        id = 1L,
        date = LocalDate.now(),
        moodScore = 5,
        moodEmoji = "😄",
        freeText = "Test entry"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getEntryByIdUseCase(1L) } returns testEntry
        coEvery { deleteEntryUseCase(any()) } returns Unit
        
        viewModel = EntryDetailViewModel(
            getEntryByIdUseCase = getEntryByIdUseCase,
            deleteEntryUseCase = deleteEntryUseCase,
            savedStateHandle = SavedStateHandle(mapOf("entryId" to 1L))
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads entry from id`() = runTest {
        advanceUntilIdle()
        assertEquals(testEntry, viewModel.uiState.value.entry)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `deleteEntry calls use case and updates isDeleted`() = runTest {
        advanceUntilIdle()
        viewModel.deleteEntry()
        advanceUntilIdle()

        coVerify { deleteEntryUseCase(testEntry) }
        assertTrue(viewModel.uiState.value.isDeleted)
    }

    @Test
    fun `loadEntry with non-existent id sets error`() = runTest {
        coEvery { getEntryByIdUseCase(1L) } returns null
        viewModel.loadEntry()
        advanceUntilIdle()

        assertEquals("Eintrag nicht gefunden.", viewModel.uiState.value.error)
    }
}
