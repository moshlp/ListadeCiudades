package com.challenge.listadeciudades.ui.screen.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
import com.challenge.listadeciudades.data.local.CiudadEntity
import com.challenge.listadeciudades.ui.screen.map.MapaScreen
import com.challenge.listadeciudades.ui.screen.home.components.BuscadorPanel
import com.challenge.listadeciudades.ui.screen.home.components.CityInfoPopup
import com.challenge.listadeciudades.viewmodel.CiudadViewModel

@Composable
fun HomeScreen(navController: NavHostController, viewModel: CiudadViewModel) {
    val ciudades by viewModel.ciudades.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val onlyFav by viewModel.onlyFavorites.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val wikiState by viewModel.wikiUiState.collectAsState()
    var showInfoPopup by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var selectedCity by remember { mutableStateOf<CiudadEntity?>(null) }

    if (isLandscape) {
        Row(Modifier.fillMaxSize()) {
            // Buscador a la izquierda
            Box(modifier = Modifier.weight(1f)) {
                BuscadorPanel(
                    ciudades = ciudades,
                    query = query,
                    onlyFav = onlyFav,
                    isLoading = uiState.isLoading,
                    onQueryChange = viewModel::updateQuery,
                    onToggleOnlyFavorites = viewModel::toggleOnlyFavorites,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onNavigateMap = {
                        selectedCity = it
                    },
                    onOpenInfo = {
                        viewModel.loadCityInfo(parseCityName(it.name))
                        showInfoPopup = true
                    }
                )
            }

            // Mapa a la derecha
            Box(modifier = Modifier.weight(1f)) {
                selectedCity?.let { ciudad ->
                    MapaScreen(
                        selectedCity = ciudad,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    } else {
        // Layout vertical (normal)
        BuscadorPanel(
            ciudades = ciudades,
            query = query,
            onlyFav = onlyFav,
            isLoading = uiState.isLoading,
            onQueryChange = viewModel::updateQuery,
            onToggleOnlyFavorites = viewModel::toggleOnlyFavorites,
            onToggleFavorite = viewModel::toggleFavorite,
            onNavigateMap = {
                navController.navigate("map/${it.name}/${it.coord.lat}/${it.coord.lon}")
            },
            onOpenInfo = {
                viewModel.loadCityInfo(parseCityName(it.name))
                showInfoPopup = true
            }
        )
    }

    // Popup de Info
    if (showInfoPopup) {
        CityInfoPopup(
            wikiState = wikiState,
            onDismiss = {
                showInfoPopup = false
                viewModel.resetWikiState()
            }
        )
    }
}

fun parseCityName(name: String): String {
    return name.replace(" ", "_")
}
