package com.challenge.listadeciudades.data.repository

import com.challenge.listadeciudades.data.local.CiudadDao
import com.challenge.listadeciudades.data.local.CiudadEntity
import kotlinx.coroutines.flow.Flow

class CiudadRepository(private val dao: CiudadDao) {

    fun getAll(): Flow<List<CiudadEntity>> = dao.getAll()

    fun searchByName(query: String): Flow<List<CiudadEntity>> = dao.searchByName(query)

    suspend fun toggleFavorite(id: Int, isFavorite: Boolean) {
        return dao.toggleFavorite(id, isFavorite)
    }

    suspend fun contarCiudades(): Int = dao.contarCiudades()

    suspend fun insertarCiudades(ciudades: List<CiudadEntity>) = dao.insertAll(ciudades)

    suspend fun limpiarCiudades() = dao.limpiarTodo()
}
