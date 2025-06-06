package com.daniel.spotyinsights.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.daniel.spotyinsights.presentation.artists.ArtistDetailScreen
import com.daniel.spotyinsights.presentation.releases.NewReleasesScreen
import com.daniel.spotyinsights.presentation.stats.StatsScreen
import com.daniel.spotyinsights.presentation.top_artists.TopArtistsScreen
import com.daniel.spotyinsights.presentation.tracks.TopTracksScreen
import com.daniel.spotyinsights.presentation.tracks.TrackDetailScreen

@Composable
fun SpotifyNavigation(
    navController: NavHostController,
    startDestination: String = Screen.TopTracks.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.TopTracks.route) {
            TopTracksScreen(navController = navController)
        }
        composable(Screen.TopArtists.route) {
            TopArtistsScreen(navController = navController)
        }
        composable(Screen.NewReleases.route) {
            NewReleasesScreen()
        }
        composable(Screen.Stats.route) {
            StatsScreen()
        }
        composable(
            route = "track_detail/{trackId}",
            arguments = listOf(navArgument("trackId") { type = NavType.StringType })
        ) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getString("trackId") ?: ""
            TrackDetailScreen(trackId = trackId, onBack = { navController.popBackStack() })
        }
        composable(
            route = "artist_detail/{artistId}",
            arguments = listOf(navArgument("artistId") { type = NavType.StringType })
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString("artistId") ?: ""
            ArtistDetailScreen(
                artistId = artistId,
                onBack = { navController.popBackStack() },
                navController = navController,
            )
        }
    }
} 