package com.challenge.listadeciudades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.challenge.listadeciudades.data.local.CiudadEntity
import com.challenge.listadeciudades.data.local.Coord
import com.challenge.listadeciudades.ui.screen.CiudadDownloadScreen
import com.challenge.listadeciudades.ui.screen.home.HomeScreen
import com.challenge.listadeciudades.ui.screen.map.MapaScreen
import com.challenge.listadeciudades.viewmodel.CiudadViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel


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
                composable("map/{name}/{lat}/{lon}") { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
                    val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull() ?: 0.0

                    val ciudad = CiudadEntity(
                        id = 0,
                        name = name,
                        country = "",
                        isFavorite = false,
                        coord = Coord(lat = lat, lon = lon)
                    )

                    MapaScreen(
                        selectedCity = ciudad,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}