package com.hung.musicstreamingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hung.musicstreamingapplication.domain.repository.LoginRepository
import com.hung.musicstreamingapplication.presentation.sign_in.SignInResult
import com.hung.musicstreamingapplication.presentation.sign_in.SignInState
import com.hung.musicstreamingapplication.presentation.sign_in.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
): ViewModel() {
    private val _signInState = MutableStateFlow(SignInState())
    val signInState = _signInState.asStateFlow()

    private val _loginState = MutableStateFlow<Boolean>(false)
    val loginState: StateFlow<Boolean> = _loginState

    private val _pressSignIn = MutableStateFlow(false)
    val pressSignIn: StateFlow<Boolean> = _pressSignIn

    val _currentUserId = MutableStateFlow(loginRepository.getCurrentUserID())
    val currentUserId: StateFlow<String?> = _currentUserId
    init{
        _currentUserId.value = loginRepository.getCurrentUserID()
    }
    fun onSignInResult(result : SignInResult){
        _signInState.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    private fun updateState(){
        _pressSignIn.value = !_pressSignIn.value
    }
    fun loginWithEmail(identity: String,password: String){
        viewModelScope.launch {
            _loginState.value = loginRepository.loginWithEmailorUsername(identity,password)
            updateState()
        }
    }
    suspend fun addUser(userData: UserData, onSuccess:(String) -> Unit, onFailure:(String)->Unit){
        loginRepository.CreateUserFromOAuth2(userData,onSuccess,onFailure)
    }
    suspend fun getUser(){
        loginRepository.GetUserFromDB()
    }
    fun resetState(){
        _signInState.update {
            SignInState()
        }
    }
    fun loginState(){
        viewModelScope.launch {
            val isLoggedIn = loginRepository.loginState()
            _loginState.value = isLoggedIn
        }
    }
}