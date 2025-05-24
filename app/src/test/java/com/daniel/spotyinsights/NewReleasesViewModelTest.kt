package com.daniel.spotyinsights

import com.daniel.spotyinsights.domain.model.NewRelease
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.usecase.releases.GetNewReleasesUseCase
import com.daniel.spotyinsights.domain.usecase.releases.RefreshNewReleasesUseCase
import com.daniel.spotyinsights.presentation.releases.NewReleasesEvent
import com.daniel.spotyinsights.presentation.releases.NewReleasesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class NewReleasesViewModelTest {
    private lateinit var getNewReleasesUseCase: GetNewReleasesUseCase
    private lateinit var refreshNewReleasesUseCase: RefreshNewReleasesUseCase
    private lateinit var viewModel: NewReleasesViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val fakeRelease = NewRelease(
        id = "1",
        name = "Test Release",
        artists = emptyList(),
        imageUrl = "img",
        releaseDate = "2020",
        spotifyUrl = "url",
        albumType = "album",
        totalTracks = 10,
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getNewReleasesUseCase = mock()
        refreshNewReleasesUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load emits loading and then success`() = runTest {
        whenever(getNewReleasesUseCase.invoke()).thenReturn(
            flow {
                emit(Result.Loading)
                emit(Result.Success(listOf(fakeRelease)))
            }
        )
        whenever(refreshNewReleasesUseCase.invoke()).thenReturn(Result.Success(Unit))
        viewModel = NewReleasesViewModel(getNewReleasesUseCase, refreshNewReleasesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assert(state.releases == listOf(fakeRelease))
    }

    @Test
    fun `refresh event triggers refresh and updates state`() = runTest {
        whenever(getNewReleasesUseCase.invoke()).thenReturn(
            flow { emit(Result.Success(listOf(fakeRelease))) }
        )
        whenever(refreshNewReleasesUseCase.invoke()).thenReturn(Result.Success(Unit))
        viewModel = NewReleasesViewModel(getNewReleasesUseCase, refreshNewReleasesUseCase)
        viewModel.setEvent(NewReleasesEvent.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assert(!state.isLoading)
        assert(state.error == null)
    }

    @Test
    fun `error from use case updates error state`() = runTest {
        whenever(getNewReleasesUseCase.invoke()).thenReturn(
            flow { emit(Result.Error(Exception("Test error"))) }
        )
        whenever(refreshNewReleasesUseCase.invoke()).thenReturn(Result.Success(Unit))
        viewModel = NewReleasesViewModel(getNewReleasesUseCase, refreshNewReleasesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assert(state.error == "Test error")
    }
} 