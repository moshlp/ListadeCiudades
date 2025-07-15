package com.challenge.listadeciudades.di

import androidx.room.Room
import com.challenge.listadeciudades.data.local.AppDatabase
import com.challenge.listadeciudades.data.remote.JsonDownloader
import com.challenge.listadeciudades.data.repository.CiudadRepository
import com.challenge.listadeciudades.viewmodel.CiudadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "ciudades_db"
        ).build()
    }
    single { get<AppDatabase>().ciudadDao() }
    single { CiudadRepository(get()) }
    single { JsonDownloader() }
    viewModel { CiudadViewModel(get(), get()) }
}