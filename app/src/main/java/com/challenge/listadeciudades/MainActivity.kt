package com.challenge.listadeciudades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.challenge.listadeciudades.ui.screen.CiudadDownloadScreen
import com.challenge.listadeciudades.viewmodel.CiudadViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = getViewModel<CiudadViewModel>()
        val jsonUrl = "https://gist.githubusercontent.com/hernan-uala/dce8843a8edbe0b0018b32e137bc2b3a/raw/0996accf70cb0ca0e16f9a99e0ee185fafca7af1/cities.json"

        viewModel.verificarYDescargar(jsonUrl)

        setContent {
            CiudadDownloadScreen(viewModel = viewModel)
        }
    }
}