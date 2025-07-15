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
}