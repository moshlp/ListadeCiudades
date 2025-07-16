package com.challenge.listadeciudades.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challenge.listadeciudades.data.model.CiudadInfoUiState
import com.challenge.listadeciudades.data.remote.WikipediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CiudadInfoViewModel(
    private val repository: WikipediaRepository
) : ViewModel() {

    private val _ciudadInfoState = MutableStateFlow(CiudadInfoUiState())
    val ciudadInfoState: StateFlow<CiudadInfoUiState> = _ciudadInfoState

    fun getCityInfo(title: String) {
        viewModelScope.launch {
            _ciudadInfoState.value = CiudadInfoUiState(isLoading = true)
            val result = repository.getCityInfo(title)
            _ciudadInfoState.value = CiudadInfoUiState(isLoading = false, cityInfo = result)
        }
    }
}