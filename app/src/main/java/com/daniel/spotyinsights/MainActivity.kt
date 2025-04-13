package com.daniel.spotyinsights

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.daniel.spotyinsights.auth.AuthStateHolder
import com.daniel.spotyinsights.auth.AuthenticationState
import com.daniel.spotyinsights.presentation.navigation.Screen
import com.daniel.spotyinsights.presentation.navigation.SpotifyBottomNavigation
import com.daniel.spotyinsights.presentation.navigation.SpotifyNavigation
import com.daniel.spotyinsights.ui.auth.AuthScreen
import com.daniel.spotyinsights.ui.theme.SpotyInsightsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authViewModel: AuthStateHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authState by authViewModel.authState.collectAsState(initial = AuthenticationState.UNAUTHENTICATED)

            SpotyInsightsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (authState) {
                        AuthenticationState.UNAUTHENTICATED -> {
                            AuthScreen(
                                onAuthSuccess = { /* State will update automatically */ }
                            )
                        }
                        AuthenticationState.AUTHENTICATED -> {
                            SpotifyApp(
                                onLogout = {
                                    authViewModel.logout()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpotifyApp(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            SpotifyBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            SpotifyNavigation(
                navController = navController,
                startDestination = Screen.TopTracks.route
            )
        }
    }
}