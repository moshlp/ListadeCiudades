package com.challenge.listadeciudades.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CiudadEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ciudadDao(): CiudadDao
}