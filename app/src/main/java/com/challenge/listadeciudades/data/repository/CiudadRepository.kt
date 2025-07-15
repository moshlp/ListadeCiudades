package com.challenge.listadeciudades.data.repository

import com.challenge.listadeciudades.data.local.CiudadDao
import com.challenge.listadeciudades.data.local.CiudadEntity

class CiudadRepository(private val dao: CiudadDao) {

    suspend fun getAll(): List<CiudadEntity> {
        return dao.getAll()
    }

    suspend fun searchByName(query: String): List<CiudadEntity> {
        return dao.searchByName(query)
    }

    suspend fun getFavorites(): List<CiudadEntity> {
        return dao.getFavorites()
    }

    suspend fun toggleFavorite(id: Int, isFavorite: Boolean) {
        dao.toggleFavorite(id, isFavorite)
    }

    suspend fun contarCiudades(): Int = dao.contarCiudades()

    suspend fun insertarCiudades(ciudades: List<CiudadEntity>) = dao.insertAll(ciudades)

    suspend fun limpiarCiudades() = dao.limpiarTodo()
}