package com.hung.musicstreamingapplication.viewmodel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor() : ViewModel() {
    private val _navController = mutableStateOf<NavController?>(null)
    val navController: NavController
        get() = _navController.value!!

    fun setNavController(navController: NavController) {
        _navController.value = navController
    }
}