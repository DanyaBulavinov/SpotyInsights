package com.daniel.spotyinsights

import app.cash.turbine.test
import com.daniel.spotyinsights.domain.model.LastFmTagDomain
import com.daniel.spotyinsights.domain.model.PlayCountPerDay
import com.daniel.spotyinsights.domain.repository.LastFmRepository
import com.daniel.spotyinsights.presentation.stats.StatsViewModel
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
class StatsViewModelTest {
    private lateinit var lastFmRepository: LastFmRepository
    private lateinit var viewModel: StatsViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val fakeTags = listOf(LastFmTagDomain("tag", 1, "url"))
    private val fakePlayCount = listOf(PlayCountPerDay("2020-01-01", 10))

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        lastFmRepository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel = StatsViewModel(lastFmRepository)
        viewModel.tags.test {
            val initial = awaitItem()
            assert(initial.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.playCountPerDay.test {
            val initial = awaitItem()
            assert(initial.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load top tags success updates state`() = runTest {
        whenever(lastFmRepository.getTopTags()).thenReturn(fakeTags)
        viewModel = StatsViewModel(lastFmRepository)
        viewModel.loadTopTags()
        viewModel.tags.test {
            skipItems(1) // initial
            val loaded = awaitItem()
            assert(loaded == fakeTags)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load play count per day success updates state`() = runTest {
        whenever(lastFmRepository.getPlayCountPerDay("Avertin21")).thenReturn(fakePlayCount)
        viewModel = StatsViewModel(lastFmRepository)
        viewModel.loadPlayCountPerDay()
        viewModel.playCountPerDay.test {
            skipItems(1) // initial
            val loaded = awaitItem()
            assert(loaded == fakePlayCount)
            cancelAndIgnoreRemainingEvents()
        }
    }
} 