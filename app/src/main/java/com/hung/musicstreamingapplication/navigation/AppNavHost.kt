// File: AppNavHost.kt
package com.hung.musicstreamingapplication.navigation

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hung.musicstreamingapplication.activity.MainActivity
import com.hung.musicstreamingapplication.presentation.sign_in.GoogleAuthUIClient
import com.hung.musicstreamingapplication.ui.screen.HeaderHome
import com.hung.musicstreamingapplication.ui.screen.SignInWithGoogleBar
import com.hung.musicstreamingapplication.ui.screen.SignUpAccount
import com.hung.musicstreamingapplication.viewmodel.LoginViewModel
import com.hung.musicstreamingapplication.viewmodel.SignUpViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
@Composable
fun AppNavHost(
    navController: NavHostController,
    googleAuth : GoogleAuthUIClient,
    viewmodel: LoginViewModel,
    signupviewmodel : SignUpViewModel,
    lifecycleScope: CoroutineScope) {
    val state by viewmodel.signInState.collectAsState()
    val loginstate by viewmodel.loginState.collectAsState()
    var hasLoggedInAttempt by remember{
        mutableStateOf(false)
    }
    val pressSignIn by viewmodel.pressSignIn.collectAsState()
    val context = LocalContext.current
    viewmodel.loginState()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LaunchedEffect(loginstate) {
                if (loginstate) {
                    // Create intent to navigate to the other Activity
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                } else {
                    navController.navigate("login")
                }
            }
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { rs ->
                    if (rs.resultCode == Activity.RESULT_OK) {
                        lifecycleScope.launch {
                            val signInResult = googleAuth.signInWithIntent(
                                intent = rs.data ?: return@launch
                            )
                            viewmodel.onSignInResult(signInResult)
                        }
                    }
                }
            )
            LaunchedEffect(key1 = state.isSignInSuccessful) {
                if (state.isSignInSuccessful) {
                    Toast.makeText(
                        context,
                        "Sign In successful",
                        Toast.LENGTH_LONG
                    ).show()
                    googleAuth.getSignedInUser()?.let { it1 ->
                        viewmodel.addUser(it1, onSuccess = {
                            Log.e("CreateUser","$it")
                            Toast.makeText(context,"Success",Toast.LENGTH_SHORT)
                        }, onFailure = { it -> Log.e("CreateUser","Failed to create user:$it")
                            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT)
                        })
                        viewmodel.getUser()
                    }
                    navController.navigate("home")
                    viewmodel.resetState()
                }
            }
            SignInWithGoogleBar(navController = navController, state = state, onSignInWithGG = {
            lifecycleScope.launch {
                val signInIntentSender = googleAuth.signIn()
                launcher.launch(
                    IntentSenderRequest.Builder(
                        signInIntentSender ?: return@launch
                    ).build()
                )
            }
        }, onSignInClick = { username,password ->
                lifecycleScope.launch {
                    viewmodel.loginWithEmail(username,password)
                }
            })

            LaunchedEffect(key1 = pressSignIn){
                if (hasLoggedInAttempt) {
                    if (loginstate) {
                        val intent = Intent(navController.context, MainActivity::class.java)
                        navController.context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Wrong password", Toast.LENGTH_SHORT).show()
                    }
                }
                hasLoggedInAttempt = true
            }
        }
        composable("signup") {
            val signUpState by signupviewmodel.signUpState.collectAsState()
            SignUpAccount(navController = navController, onSignUpClick = {
                email,username,password ->
                lifecycleScope.launch{
                    signupviewmodel.updateSignUpState(email,username,password)
                }
            })
            LaunchedEffect(key1 = signUpState){
                if(signUpState){
                    Toast.makeText(context,"Sign Up Successfully!",Toast.LENGTH_SHORT).show()
                    val intent = Intent(navController.context,MainActivity::class.java)
                    navController.context.startActivity(intent)
                }
            }
        }
//        composable("Home"){ HeaderHome()}
    }
}
