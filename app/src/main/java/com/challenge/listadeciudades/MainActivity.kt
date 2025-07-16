package com.challenge.listadeciudades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.challenge.listadeciudades.ui.screen.CiudadDownloadScreen
import com.challenge.listadeciudades.viewmodel.CiudadViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import androidx.navigation.compose.rememberNavController
import com.challenge.listadeciudades.ui.screen.HomeScreen
import com.challenge.listadeciudades.ui.screen.MapaScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = getViewModel<CiudadViewModel>()
        val jsonUrl = "https://gist.githubusercontent.com/hernan-uala/dce8843a8edbe0b0018b32e137bc2b3a/raw/0996accf70cb0ca0e16f9a99e0ee185fafca7af1/cities.json"

        viewModel.verificarYDescargar(jsonUrl)

        setContent {
            val navController = rememberNavController()
            val isReady by viewModel.isReady.collectAsState()

            LaunchedEffect(isReady) {
                if (isReady) {
                    navController.navigate("home") {
                        popUpTo("loader") { inclusive = true }
                    }
                }
            }

            NavHost(navController = navController, startDestination = "loader") {
                composable("loader") {
                    CiudadDownloadScreen(viewModel = viewModel)
                }
                composable("home") {
                    HomeScreen(navController = navController, viewModel = viewModel)
                }
                composable("map/{lon}/{lat}") { backStackEntry ->
                    val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull() ?: 0.0
                    val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
                    MapaScreen(
                        lon = lon,
                        lat = lat,
                        onBack = { navController.popBackStack() }
                    )
                }

            }
        }
    }
}