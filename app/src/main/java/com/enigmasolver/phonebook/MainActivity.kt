package com.enigmasolver.phonebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.enigmasolver.phonebook.shared.AppEvent
import com.enigmasolver.phonebook.shared.AppEventBus
import com.enigmasolver.phonebook.shared.components.SuccessToast
import com.enigmasolver.phonebook.shared.router.AppRouter
import com.enigmasolver.phonebook.shared.theme.PhoneBookTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var eventBus: AppEventBus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(true) {
                eventBus.events.collect { event ->
                    if (event is AppEvent.ShowSuccessSnackbar) {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }

            PhoneBookTheme {
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                            SuccessToast(message = data.visuals.message)
                        }
                    }
                ) {
                    AppRouter()
                }
            }
        }
    }
}

