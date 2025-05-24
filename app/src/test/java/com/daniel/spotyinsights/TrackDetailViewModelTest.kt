package com.daniel.spotyinsights

import app.cash.turbine.test
import com.daniel.spotyinsights.domain.model.Track
import com.daniel.spotyinsights.domain.model.Album
import com.daniel.spotyinsights.domain.model.TrackArtist
import com.daniel.spotyinsights.domain.repository.TrackRepository
import com.daniel.spotyinsights.presentation.tracks.TrackDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class TrackDetailViewModelTest {
    private lateinit var trackRepository: TrackRepository
    private lateinit var viewModel: TrackDetailViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val fakeTrack = Track(
        id = "1",
        name = "Test Track",
        artists = listOf(TrackArtist("1", "Artist", "url")),
        album = Album("1", "A", "2020", "img", "url"),
        durationMs = 1000,
        popularity = 10,
        previewUrl = null,
        spotifyUrl = "url",
        explicit = false
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        trackRepository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel = TrackDetailViewModel(trackRepository)
        viewModel.track.test {
            val initial = awaitItem()
            assert(initial == null)
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.isLoading.test {
            val initial = awaitItem()
            assert(!initial)
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.error.test {
            val initial = awaitItem()
            assert(initial == null)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load track details success updates state`() = runTest {
        whenever(trackRepository.getTrackById("1")).thenReturn(fakeTrack)
        viewModel = TrackDetailViewModel(trackRepository)
        viewModel.loadTrackDetails("1")
        testDispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.track.value == fakeTrack)
    }

    @Test
    fun `load track details error updates error state`() = runTest {
        whenever(trackRepository.getTrackById("1")).thenThrow(RuntimeException("Test error"))
        viewModel = TrackDetailViewModel(trackRepository)
        viewModel.loadTrackDetails("1")
        testDispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.error.value == "Test error")
    }
} 