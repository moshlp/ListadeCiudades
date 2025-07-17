package com.challenge.listadeciudades.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CiudadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ciudades: List<CiudadEntity>)

    @Query("SELECT COUNT(*) FROM ciudades")
    suspend fun contarCiudades(): Int

    @Query("DELETE FROM ciudades")
    suspend fun limpiarTodo()

    @Query("SELECT * FROM ciudades ORDER BY name ASC")
    fun getAll(): Flow<List<CiudadEntity>>

    @Query("SELECT * FROM ciudades WHERE name LIKE :query || '%' ORDER BY name ASC")
    fun searchByName(query: String): Flow<List<CiudadEntity>>

    @Query("UPDATE ciudades SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Int, isFavorite: Boolean)
}
