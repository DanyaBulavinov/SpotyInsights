package com.daniel.spotyinsights.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.daniel.spotyinsights.presentation.releases.NewReleasesScreen
import com.daniel.spotyinsights.presentation.top_artists.TopArtistsScreen
import com.daniel.spotyinsights.presentation.tracks.TopTracksScreen

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
            TopTracksScreen()
        }
        composable(Screen.TopArtists.route) {
            TopArtistsScreen()
        }
        composable(Screen.NewReleases.route) {
            NewReleasesScreen()
        }
    }
} 