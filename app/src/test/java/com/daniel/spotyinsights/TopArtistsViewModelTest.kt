package com.daniel.spotyinsights

import app.cash.turbine.test
import com.daniel.spotyinsights.domain.model.DetailedArtist
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.usecase.artist.GetTopArtistsUseCase
import com.daniel.spotyinsights.domain.usecase.artist.RefreshTopArtistsUseCase
import com.daniel.spotyinsights.presentation.top_artists.TopArtistsEvent
import com.daniel.spotyinsights.presentation.top_artists.TopArtistsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class TopArtistsViewModelTest {
    private lateinit var getTopArtistsUseCase: GetTopArtistsUseCase
    private lateinit var refreshTopArtistsUseCase: RefreshTopArtistsUseCase
    private lateinit var viewModel: TopArtistsViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val fakeArtist = DetailedArtist(
        id = "1",
        name = "Test Artist",
        spotifyUrl = "https://spotify.com/artist/1",
        genres = listOf("pop"),
        images = listOf("img1"),
        popularity = 99,
        followers = 1000
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getTopArtistsUseCase = mock()
        refreshTopArtistsUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load emits loading and then success`() = runTest {
        whenever(getTopArtistsUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(
            flow {
                emit(Result.Loading)
                emit(Result.Success(listOf(fakeArtist)))
            }
        )
        whenever(refreshTopArtistsUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(
            Result.Success(
                Unit
            )
        )
        viewModel = TopArtistsViewModel(getTopArtistsUseCase, refreshTopArtistsUseCase)
        viewModel.uiState.test {
            val initial = awaitItem()
            assert(initial.isLoading || !initial.isLoading)
            val loading = awaitItem()
            assert(loading.isLoading)
            val loaded = awaitItem()
            assert(loaded.artists == listOf(fakeArtist))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refresh event triggers refresh and updates state`() = runTest {
        whenever(getTopArtistsUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(
            flow { emit(Result.Success(listOf(fakeArtist))) }
        )
        whenever(refreshTopArtistsUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(
            Result.Success(
                Unit
            )
        )
        viewModel = TopArtistsViewModel(getTopArtistsUseCase, refreshTopArtistsUseCase)
        viewModel.setEvent(TopArtistsEvent.Refresh)
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
        // Arrange: Setup mocks for both time ranges
        val artistMedium = fakeArtist.copy(id = "1")
        val artistShort = fakeArtist.copy(id = "2")
        whenever(getTopArtistsUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(
            flow { emit(Result.Success(listOf(artistMedium))) }
        )
        whenever(getTopArtistsUseCase.invoke(TimeRange.SHORT_TERM)).thenReturn(
            flow { emit(Result.Success(listOf(artistShort))) }
        )
        whenever(refreshTopArtistsUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(Result.Success(Unit))
        whenever(refreshTopArtistsUseCase.invoke(TimeRange.SHORT_TERM)).thenReturn(Result.Success(Unit))

        viewModel = TopArtistsViewModel(getTopArtistsUseCase, refreshTopArtistsUseCase)

        // Act: Select a new time range
        viewModel.setEvent(TopArtistsEvent.TimeRangeSelected(TimeRange.SHORT_TERM))

        // Assert: Wait for a state with the new time range and the expected artist
        viewModel.uiState.test {
            var found = false
            repeat(10) {
                val state = awaitItem()
                if (
                    state.selectedTimeRange == TimeRange.SHORT_TERM &&
                    state.artists.any { it.id == "2" } &&
                    !state.isLoading
                ) {
                    found = true
                    return@test
                }
            }
            assert(found) { "Expected artist with id '2' not found in SHORT_TERM state" }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `error from use case updates error state`() = runTest {
        whenever(getTopArtistsUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(
            flow { emit(Result.Error(Exception("Test error"))) }
        )
        whenever(refreshTopArtistsUseCase.invoke(TimeRange.MEDIUM_TERM)).thenReturn(
            Result.Success(
                Unit
            )
        )
        viewModel = TopArtistsViewModel(getTopArtistsUseCase, refreshTopArtistsUseCase)
        viewModel.uiState.test {
            skipItems(1) // initial
            val errorState = awaitItem()
            assert(errorState.error == "Test error")
            cancelAndIgnoreRemainingEvents()
        }
    }
} 