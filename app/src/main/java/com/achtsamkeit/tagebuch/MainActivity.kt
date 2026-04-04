package com.achtsamkeit.tagebuch

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.achtsamkeit.tagebuch.core.navigation.AchtsamkeitNavGraph
import com.achtsamkeit.tagebuch.presentation.security.LockScreen
import com.achtsamkeit.tagebuch.presentation.security.SecurityViewModel
import com.achtsamkeit.tagebuch.ui.theme.AchtsamkeitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AchtsamkeitTheme {
                val securityViewModel: SecurityViewModel = viewModel()
                val isAuthenticated by securityViewModel.isAuthenticated.collectAsState()

                if (isAuthenticated) {
                    AchtsamkeitNavGraph()
                } else {
                    LockScreen(
                        viewModel = securityViewModel,
                        onAuthenticated = { /* Wird über State gesteuert */ }
                    )
                }
            }
        }
    }
}
