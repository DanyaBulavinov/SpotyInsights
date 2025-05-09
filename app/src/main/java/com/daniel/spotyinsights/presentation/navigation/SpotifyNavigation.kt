package com.daniel.spotyinsights.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.daniel.spotyinsights.presentation.releases.NewReleasesScreen
import androidx.navigation.navArgument
import com.daniel.spotyinsights.presentation.recommendations.RecommendationsScreen
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
            TopArtistsScreen()
        }
        composable(Screen.NewReleases.route) {
            NewReleasesScreen()
        }
        composable(
            route = "track_detail/{trackId}",
            arguments = listOf(navArgument("trackId") { type = NavType.StringType })
        ) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getString("trackId") ?: ""
            TrackDetailScreen(trackId = trackId, onBack = { navController.popBackStack() })
        }
    }
} 