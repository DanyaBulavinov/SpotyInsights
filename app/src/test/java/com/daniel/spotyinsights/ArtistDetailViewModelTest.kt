package com.daniel.spotyinsights

import app.cash.turbine.test
import com.daniel.spotyinsights.domain.model.DetailedArtist
import com.daniel.spotyinsights.domain.model.Track
import com.daniel.spotyinsights.domain.usecase.artist.GetArtistByIdUseCase
import com.daniel.spotyinsights.domain.usecase.artist.GetArtistTopTracksUseCase
import com.daniel.spotyinsights.presentation.artists.ArtistDetailEvent
import com.daniel.spotyinsights.presentation.artists.ArtistDetailViewModel
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
class ArtistDetailViewModelTest {
    private lateinit var getArtistByIdUseCase: GetArtistByIdUseCase
    private lateinit var getArtistTopTracksUseCase: GetArtistTopTracksUseCase
    private lateinit var viewModel: ArtistDetailViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val fakeArtist = DetailedArtist(
        id = "1",
        name = "Test Artist",
        spotifyUrl = "url",
        genres = listOf("pop"),
        images = listOf("img"),
        popularity = 10,
        followers = 100
    )
    private val fakeTracks = listOf<Track>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getArtistByIdUseCase = mock()
        getArtistTopTracksUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel = ArtistDetailViewModel(getArtistByIdUseCase, getArtistTopTracksUseCase)
        viewModel.uiState.test {
            val initial = awaitItem()
            assert(initial.artist == null)
            assert(initial.topTracks.isEmpty())
            assert(!initial.isLoading)
            assert(initial.error == null)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load artist success updates state`() = runTest {
        whenever(getArtistByIdUseCase.invoke("1")).thenReturn(fakeArtist)
        whenever(getArtistTopTracksUseCase.invoke("1")).thenReturn(fakeTracks)
        viewModel = ArtistDetailViewModel(getArtistByIdUseCase, getArtistTopTracksUseCase)
        viewModel.setEvent(ArtistDetailEvent.LoadArtist("1"))
        viewModel.uiState.test {
            skipItems(1) // initial
            val loading = awaitItem()
            assert(loading.isLoading)
            val loaded = awaitItem()
            assert(loaded.artist == fakeArtist)
            assert(loaded.topTracks == fakeTracks)
            assert(!loaded.isLoading)
            assert(loaded.error == null)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load artist error updates error state`() = runTest {
        whenever(getArtistByIdUseCase.invoke("1")).thenThrow(RuntimeException("Test error"))
        viewModel = ArtistDetailViewModel(getArtistByIdUseCase, getArtistTopTracksUseCase)
        viewModel.setEvent(ArtistDetailEvent.LoadArtist("1"))
        viewModel.uiState.test {
            skipItems(2) // initial, loading
            val errorState = awaitItem()
            assert(errorState.error == "Test error")
            assert(!errorState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }
} 