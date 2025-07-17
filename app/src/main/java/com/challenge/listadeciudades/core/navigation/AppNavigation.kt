package com.challenge.listadeciudades.core.navigation

import androidx.compose.runtime.Composable
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

@Composable
fun AppNavigation(viewModel: CiudadViewModel) {
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
