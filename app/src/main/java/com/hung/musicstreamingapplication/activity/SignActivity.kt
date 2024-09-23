package com.hung.musicstreamingapplication.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.hung.musicstreamingapplication.presentation.sign_in.GoogleAuthUIClient
import com.hung.musicstreamingapplication.navigation.AppNavHost
import com.hung.musicstreamingapplication.ui.theme.MusicStreamingApplicationTheme
import com.hung.musicstreamingapplication.viewmodel.LoginViewModel
import com.hung.musicstreamingapplication.viewmodel.NavViewModel
import com.hung.musicstreamingapplication.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicStreamingApplicationTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navViewModel = hiltViewModel<NavViewModel>()
                    val navController = rememberNavController()
                    LaunchedEffect(Unit){
                        navViewModel.setNavController(navController)
                    }
                    val viewmodel = hiltViewModel<LoginViewModel>()
                    val signUpviewmodel = hiltViewModel<SignUpViewModel>()

                    AppNavHost(navController = navController, googleAuth = googleAuthUIClient, viewmodel = viewmodel,signUpviewmodel, lifecycleScope = lifecycleScope)
                }
            }
        }
    }
    private val googleAuthUIClient by lazy {
        GoogleAuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
}