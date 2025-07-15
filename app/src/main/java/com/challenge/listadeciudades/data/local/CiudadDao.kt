package com.challenge.listadeciudades.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CiudadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ciudades: List<CiudadEntity>)

    @Query("SELECT COUNT(*) FROM ciudades")
    suspend fun contarCiudades(): Int

    @Query("DELETE FROM ciudades")
    suspend fun limpiarTodo()

    @Query("SELECT * FROM ciudades ORDER BY name ASC")
    suspend fun getAll(): List<CiudadEntity>

    @Query("SELECT * FROM ciudades WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchByName(query: String): List<CiudadEntity>

    @Query("SELECT * FROM ciudades WHERE isFavorite = 1 ORDER BY name ASC")
    suspend fun getFavorites(): List<CiudadEntity>

    @Query("UPDATE ciudades SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Int, isFavorite: Boolean)
}