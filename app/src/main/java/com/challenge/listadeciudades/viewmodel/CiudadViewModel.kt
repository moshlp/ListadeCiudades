package com.challenge.listadeciudades.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challenge.listadeciudades.data.remote.JsonDownloader
import com.challenge.listadeciudades.data.repository.CiudadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CiudadViewModel(
    private val repository: CiudadRepository,
    private val jsonDownloader: JsonDownloader
) : ViewModel() {

    private val _progreso = MutableStateFlow(0)
    val progreso: StateFlow<Int> get() = _progreso

    fun verificarYDescargar(url: String) {
        viewModelScope.launch {
            val count = repository.contarCiudades()
            if (count == 0) {
                jsonDownloader.descargarCiudades(url).collect { (porcentaje, ciudades) ->
                    _progreso.value = porcentaje
                    if (porcentaje == 100) {
                        repository.limpiarCiudades()
                        repository.insertarCiudades(ciudades)
                    }
                }
            }
        }
    }
}