package com.challenge.listadeciudades.data.model

import com.challenge.listadeciudades.data.remote.model.WikipediaResponse

data class CiudadInfoUiState(
    val isLoading: Boolean = false,
    val cityInfo: WikipediaResponse? = null
)