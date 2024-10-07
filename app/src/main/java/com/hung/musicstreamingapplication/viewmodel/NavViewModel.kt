package com.hung.musicstreamingapplication.viewmodel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor() : ViewModel() {
    private val _navController = mutableStateOf<NavController?>(null)
    private val _itemSelected = MutableStateFlow("home")
    val itemSelected = _itemSelected.asStateFlow()
    val navController: NavController
        get() = _navController.value!!

    fun setNavController(navController: NavController) {
        _navController.value = navController
    }
    fun setItemNavBar(item: String){
        _itemSelected.update {
            item
        }
    }
}