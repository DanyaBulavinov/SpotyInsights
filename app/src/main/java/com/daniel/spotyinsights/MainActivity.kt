package com.daniel.spotyinsights

import android.content.Intent
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
import androidx.compose.runtime.LaunchedEffect
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
import com.daniel.spotyinsights.ui.auth.AuthViewModel
import com.daniel.spotyinsights.ui.theme.SpotyInsightsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authViewModel: AuthStateHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
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
                                onAuthSuccess = {
                                    // This will trigger a recomposition when auth state changes
                                    // No need for explicit navigation as the state change will
                                    // automatically switch to the authenticated content
                                }
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        // Handle the redirect URI from Spotify
        intent?.data?.let { uri ->
            if (uri.toString().startsWith("spotyinsights://callback")) {
                // Extract the authorization code from the URI
                uri.getQueryParameter("code")?.let { code ->
                    // Find the AuthViewModel and handle the code
                    val authViewModel = findAuthViewModel()
                    authViewModel?.setEvent(com.daniel.spotyinsights.ui.auth.AuthEvent.HandleAuthResponse(code))
                }
            }
        }
    }

    private fun findAuthViewModel(): AuthViewModel? {
        return try {
            // Get the NavHostFragment's current AuthScreen if it exists
            // and retrieve its ViewModel
            val viewModelStoreOwner = this
            androidx.lifecycle.ViewModelProvider(viewModelStoreOwner)[AuthViewModel::class.java]
        } catch (e: Exception) {
            null
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