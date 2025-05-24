package com.daniel.spotyinsights

import app.cash.turbine.test
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.model.Track
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.usecase.tracks.GetTopTracksUseCase
import com.daniel.spotyinsights.domain.usecase.tracks.RefreshTopTracksUseCase
import com.daniel.spotyinsights.presentation.tracks.TopTracksEvent
import com.daniel.spotyinsights.presentation.tracks.TopTracksViewModel
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
class TopTracksViewModelTest {
    private lateinit var getTopTracksUseCase: GetTopTracksUseCase
    private lateinit var refreshTopTracksUseCase: RefreshTopTracksUseCase
    private lateinit var viewModel: TopTracksViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val fakeTrack = Track(
        id = "1",
        name = "Test Track",
        artists = emptyList(),
        album = com.daniel.spotyinsights.domain.model.Album("1", "A", "2020", "img", "url"),
        durationMs = 1000,
        popularity = 10,
        previewUrl = null,
        spotifyUrl = "url",
        explicit = false
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getTopTracksUseCase = mock()
        refreshTopTracksUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load emits loading and then success`() = runTest {
        whenever(getTopTracksUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(
            flow {
                emit(Result.Loading)
                emit(Result.Success(listOf(fakeTrack)))
            }
        )
        whenever(refreshTopTracksUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(Result.Success(Unit))
        viewModel = TopTracksViewModel(getTopTracksUseCase, refreshTopTracksUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            var found = false
            repeat(10) {
                val state = awaitItem()
                if (!state.isLoading && state.tracks == listOf(fakeTrack)) {
                    found = true
                    return@test // exit the test block
                }
            }
            assert(found) { "Expected state with loaded tracks not found" }
        }
    }

    @Test
    fun `refresh event triggers refresh and updates state`() = runTest {
        whenever(getTopTracksUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(
            flow { emit(Result.Success(listOf(fakeTrack))) }
        )
        whenever(refreshTopTracksUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(Result.Success(Unit))
        viewModel = TopTracksViewModel(getTopTracksUseCase, refreshTopTracksUseCase)
        viewModel.setEvent(TopTracksEvent.Refresh)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1) // initial
            val refreshed = awaitItem()
            assert(!refreshed.isLoading)
            assert(refreshed.error == null)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `time range selection triggers state update and refresh`() = runTest {
        val trackMedium = fakeTrack.copy(id = "1")
        val trackShort = fakeTrack.copy(id = "2")
        whenever(getTopTracksUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(
            flow { emit(Result.Success(listOf(trackMedium))) }
        )
        whenever(getTopTracksUseCase.invoke(TimeRange.SHORT_TERM)).thenReturn(
            flow { emit(Result.Success(listOf(trackShort))) }
        )
        whenever(refreshTopTracksUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(Result.Success(Unit))
        whenever(refreshTopTracksUseCase.invoke(TimeRange.SHORT_TERM)).thenReturn(Result.Success(Unit))
        viewModel = TopTracksViewModel(getTopTracksUseCase, refreshTopTracksUseCase)
        viewModel.setEvent(TopTracksEvent.TimeRangeSelected(TimeRange.SHORT_TERM))

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            var found = false
            repeat(10) {
                val state = awaitItem()
                if (
                    state.selectedTimeRange == TimeRange.SHORT_TERM &&
                    state.tracks.any { it.id == "2" } &&
                    !state.isLoading
                ) {
                    found = true
                    return@test // exit the test block
                }
            }
            assert(found) { "Expected track with id '2' not found in SHORT_TERM state" }
        }
    }

    @Test
    fun `error from use case updates error state`() = runTest {
        whenever(getTopTracksUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(
            flow { emit(Result.Error(Exception("Test error"))) }
        )
        whenever(refreshTopTracksUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(Result.Success(Unit))
        viewModel = TopTracksViewModel(getTopTracksUseCase, refreshTopTracksUseCase)
        viewModel.uiState.test {
            skipItems(1) // initial
            val errorState = awaitItem()
            assert(errorState.error == "Test error")
            cancelAndIgnoreRemainingEvents()
        }
    }
} 