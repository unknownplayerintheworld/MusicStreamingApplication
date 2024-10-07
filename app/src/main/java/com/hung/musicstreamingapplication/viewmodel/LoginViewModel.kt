package com.hung.musicstreamingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hung.musicstreamingapplication.data.model.User
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

    private val _loginState = MutableStateFlow(false)
    val loginState: StateFlow<Boolean> = _loginState

    private val _pressSignIn = MutableStateFlow(false)
    val pressSignIn: StateFlow<Boolean> = _pressSignIn

    val _currentUserId = MutableStateFlow(loginRepository.getCurrentUserID())
    private val _currentUser = MutableStateFlow<User?>(User())
    val currentUser = _currentUser.asStateFlow()
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
    fun getUser(userID: String){
        viewModelScope.launch {
            loginRepository.GetUserFromDB(userID).let {  user ->
                _currentUser.update {
                    user
                }
            }
        }
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
    fun logout(){
        viewModelScope.launch {
            loginRepository.logout().let { b ->
                _loginState.update { b }

                if (!_loginState.value) {  // Kiểm tra giá trị _loginState
                    _currentUser.update { null }
                    _currentUserId.update { "" }
                }
            }
        }
    }
}