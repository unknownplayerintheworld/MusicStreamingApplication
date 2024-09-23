package com.hung.musicstreamingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hung.musicstreamingapplication.domain.repository.SignUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpRepository: SignUpRepository
) :ViewModel(){
    private val _signUpState = MutableStateFlow(false)
    val signUpState = _signUpState.asStateFlow()

    suspend fun signUp(email: String,username: String,password: String): Boolean{
        return signUpRepository.signUp(email,username,password)
    }
    fun updateSignUpState(email: String,username: String,password: String){
        viewModelScope.launch {
            _signUpState.value = signUp(email,username,password)
        }
    }
}