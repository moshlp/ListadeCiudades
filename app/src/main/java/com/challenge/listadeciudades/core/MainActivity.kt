package com.challenge.listadeciudades.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.challenge.listadeciudades.core.navigation.AppNavigation
import com.challenge.listadeciudades.viewmodel.CiudadViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

class MainActivity : ComponentActivity() {

    private val jsonUrl: String by inject(String::class.java, named("citiesJsonUrl"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = getViewModel<CiudadViewModel>()

        viewModel.verificarYDescargar(jsonUrl)

        setContent {
            AppNavigation(viewModel = viewModel)
        }
    }
}
