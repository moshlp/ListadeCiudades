package com.challenge.listadeciudades.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challenge.listadeciudades.data.local.CiudadEntity
import com.challenge.listadeciudades.data.model.CiudadUiState
import com.challenge.listadeciudades.data.remote.JsonDownloader
import com.challenge.listadeciudades.data.repository.CiudadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CiudadViewModel(
    private val repository: CiudadRepository,
    private val jsonDownloader: JsonDownloader
) : ViewModel() {

    private val _progreso = MutableStateFlow(0)
    val progreso: StateFlow<Int> get() = _progreso

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> get() = _isReady

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites

    private val _uiState = MutableStateFlow(CiudadUiState())
    val uiState: StateFlow<CiudadUiState> get() = _uiState

    init {
        combine(
            _searchQuery,
            _onlyFavorites,
            _isReady
        ) { query, onlyFav, ready ->
            if (ready) {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val ciudades = if (onlyFav) {
                    repository.getFavorites()
                } else if (query.isBlank()) {
                    repository.getAll()
                } else {
                    repository.searchByName(query)
                }
                _uiState.value = _uiState.value.copy(
                    ciudades = ciudades.sortedBy { it.name },
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    fun verificarYDescargar(url: String) {
        viewModelScope.launch {
            val count = repository.contarCiudades()
            if (count == 0) {
                jsonDownloader.descargarCiudades(url).collect { (porcentaje, ciudades) ->
                    _progreso.value = porcentaje
                    if (porcentaje == 100) {
                        repository.limpiarCiudades()
                        repository.insertarCiudades(ciudades)
                        _isReady.value = true
                    }
                }
            } else {
                _isReady.value = true
            }
        }
    }

    val ciudades = combine(_searchQuery, _onlyFavorites) { query, onlyFav ->
        if (onlyFav) {
            repository.getFavorites()
        } else if (query.isBlank()) {
            repository.getAll()
        } else {
            repository.searchByName(query)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleOnlyFavorites() {
        _onlyFavorites.value = !_onlyFavorites.value
    }

    fun toggleFavorite(ciudad: CiudadEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(ciudad.id, !ciudad.isFavorite)
            val refreshed = if (_onlyFavorites.value) {
                repository.getFavorites()
            } else {
                repository.getAll()
            }
            _uiState.value = _uiState.value.copy(ciudades = refreshed.sortedBy { it.name })
        }
    }
}
