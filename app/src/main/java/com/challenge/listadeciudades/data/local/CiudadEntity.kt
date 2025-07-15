package com.challenge.listadeciudades.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Embedded
import com.google.gson.annotations.SerializedName

@Entity(tableName = "ciudades")
data class CiudadEntity(
    @PrimaryKey
    @SerializedName("_id") val id: Int,
    val name: String,
    val country: String,
    @Embedded val coord: Coord,
    val isFavorite: Boolean = false,
)

data class Coord(
    val lon: Double,
    val lat: Double
)