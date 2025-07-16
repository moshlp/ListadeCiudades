package com.challenge.listadeciudades.di

import androidx.room.Room
import com.challenge.listadeciudades.data.local.AppDatabase
import com.challenge.listadeciudades.data.remote.JsonDownloader
import com.challenge.listadeciudades.data.remote.WikipediaApiService
import com.challenge.listadeciudades.data.remote.WikipediaRepository
import com.challenge.listadeciudades.data.repository.CiudadRepository
import com.challenge.listadeciudades.viewmodel.CiudadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    single { WikipediaRepository(get()) }

    viewModel { CiudadViewModel(get(), get(), get()) }

    single {
        Retrofit.Builder()
            .baseUrl("https://en.wikipedia.org/api/rest_v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WikipediaApiService::class.java)
    }

}