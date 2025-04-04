package com.daniel.spotyinsights.presentation.navigation

sealed class Screen(val route: String) {
    object TopTracks : Screen("top_tracks")
    object TopArtists : Screen("top_artists")
    object Recommendations : Screen("recommendations")

    companion object {
        val bottomNavItems = listOf(
            TopTracks,
            TopArtists,
            Recommendations
        )
    }
} 