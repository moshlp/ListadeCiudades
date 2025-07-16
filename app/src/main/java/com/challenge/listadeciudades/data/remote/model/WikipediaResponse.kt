package com.challenge.listadeciudades.data.remote.model

data class WikipediaResponse(
    val title: String?,
    val extract: String?,
    val thumbnail: Thumbnail?
)

data class Thumbnail(
    val source: String?
)