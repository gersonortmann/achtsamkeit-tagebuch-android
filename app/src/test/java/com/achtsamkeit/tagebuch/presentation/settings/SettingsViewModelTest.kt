package com.achtsamkeit.tagebuch.presentation.settings

import com.achtsamkeit.tagebuch.domain.model.GuidedQuestion
import com.achtsamkeit.tagebuch.domain.model.ThemeConfig
import com.achtsamkeit.tagebuch.domain.repository.AchtsamkeitRepository
import com.achtsamkeit.tagebuch.domain.usecase.GetAllQuestionsUseCase
import com.achtsamkeit.tagebuch.domain.usecase.UpdateQuestionUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val repository = mockk<AchtsamkeitRepository>()
    private val getAllQuestionsUseCase = mockk<GetAllQuestionsUseCase>()
    private val updateQuestionUseCase = mockk<UpdateQuestionUseCase>()
    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testQuestions = listOf(
        GuidedQuestion(id = 1, question = "Wofür bist du heute dankbar?", isSelected = true),
        GuidedQuestion(id = 2, question = "Was war dein Erfolg?", isSelected = false)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.themeConfig } returns flowOf(ThemeConfig.FOLLOW_SYSTEM)
        every { getAllQuestionsUseCase() } returns flowOf(testQuestions)
        
        viewModel = SettingsViewModel(repository, getAllQuestionsUseCase, updateQuestionUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads theme and questions`() = runTest {
        advanceUntilIdle()
        assertEquals(ThemeConfig.FOLLOW_SYSTEM, viewModel.themeConfig.value)
        assertEquals(2, viewModel.questions.value.size)
    }

    @Test
    fun `toggleQuestionSelection calls update use case with toggled value`() = runTest {
        val questionToToggle = testQuestions[0]
        coEvery { updateQuestionUseCase(any()) } returns Unit
        
        viewModel.toggleQuestionSelection(questionToToggle)
        advanceUntilIdle()
        
        coVerify { 
            updateQuestionUseCase(match { it.id == 1L && !it.isSelected }) 
        }
    }

    @Test
    fun `setThemeConfig updates repository`() = runTest {
        coEvery { repository.setThemeConfig(any()) } returns Unit
        
        viewModel.setThemeConfig(ThemeConfig.DARK)
        advanceUntilIdle()
        
        coVerify { repository.setThemeConfig(ThemeConfig.DARK) }
    }
}
