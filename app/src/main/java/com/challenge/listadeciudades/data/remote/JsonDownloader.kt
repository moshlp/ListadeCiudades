package com.challenge.listadeciudades.data.remote

import com.challenge.listadeciudades.data.local.CiudadEntity
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStreamReader

class JsonDownloader {

    private val client = OkHttpClient()
    private val gson = Gson()

    fun descargarCiudades(url: String): Flow<Pair<Int, List<CiudadEntity>>> = flow {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val totalBytes = response.body?.contentLength() ?: -1L
        val inputStream = response.body?.byteStream() ?: return@flow

        val reader = JsonReader(InputStreamReader(inputStream))
        val ciudades = mutableListOf<CiudadEntity>()

        reader.beginArray()
        var bytesRead = 0L
        while (reader.hasNext()) {
            val ciudad = gson.fromJson<CiudadEntity>(reader, CiudadEntity::class.java)
            ciudades.add(ciudad)
            bytesRead += 1

            if (totalBytes > 0) {
                val porcentaje = ((bytesRead.toDouble() / totalBytes.toDouble()) * 100).toInt()
                emit(porcentaje to ciudades.toList())
            }
        }
        reader.endArray()
        emit(100 to ciudades)
    }.flowOn(Dispatchers.IO)
}