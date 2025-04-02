package com.daniel.spotyinsights

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.daniel.spotyinsights.auth.AuthStateHolder
import com.daniel.spotyinsights.auth.AuthenticationState
import com.daniel.spotyinsights.ui.auth.AuthEvent
import com.daniel.spotyinsights.ui.auth.AuthScreen
import com.daniel.spotyinsights.ui.auth.AuthViewModel
import com.daniel.spotyinsights.ui.main.MainContent
import com.daniel.spotyinsights.ui.theme.SpotyInsightsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authViewModel: AuthViewModel

    @Inject
    lateinit var authStateHolder: AuthStateHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Check for existing authentication
        authViewModel.setEvent(AuthEvent.CheckAuth)
        
        handleIntent(intent)
        
        setContent {
            val authState by authStateHolder.authState.collectAsState(initial = AuthenticationState.UNAUTHENTICATED)
            
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
                            MainContent(
                                onLogout = {
                                    authViewModel.setEvent(AuthEvent.Logout)
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
        if (intent?.action == Intent.ACTION_VIEW) {
            val uri = intent.data
            if (uri?.scheme == "spotyinsights" && uri.host == "callback") {
                // Extract the authorization code from the URI
                uri.getQueryParameter("code")?.let { code ->
                    authViewModel.setEvent(AuthEvent.HandleAuthResponse(code))
                }
                
                uri.getQueryParameter("error")?.let { error ->
                    // Handle error case if needed
                }
            }
        }
    }
}