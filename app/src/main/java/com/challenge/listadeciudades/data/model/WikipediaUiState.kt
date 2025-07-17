package com.challenge.listadeciudades.data.model

data class WikipediaUiState(
    val title: String = "",
    val extract: String = "",
    val thumbnailUrl: String? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)
