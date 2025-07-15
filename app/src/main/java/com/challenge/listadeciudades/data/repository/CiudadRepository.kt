package com.challenge.listadeciudades.data.repository

import com.challenge.listadeciudades.data.local.CiudadDao
import com.challenge.listadeciudades.data.local.CiudadEntity

class CiudadRepository(private val dao: CiudadDao) {
    suspend fun contarCiudades(): Int = dao.contarCiudades()
    suspend fun insertarCiudades(ciudades: List<CiudadEntity>) = dao.insertAll(ciudades)
    suspend fun limpiarCiudades() = dao.limpiarTodo()}