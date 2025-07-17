package com.challenge.listadeciudades.data.model

import com.challenge.listadeciudades.data.local.CiudadEntity

data class CiudadUiState(
    val isLoading: Boolean = true,
    val ciudades: List<CiudadEntity> = emptyList()
)
