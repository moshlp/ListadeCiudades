package com.challenge.listadeciudades.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.challenge.listadeciudades.data.local.CiudadEntity
import com.challenge.listadeciudades.viewmodel.CiudadViewModel

@Composable
fun HomeScreen(navController: NavHostController, viewModel: CiudadViewModel) {
    val ciudades by viewModel.ciudades.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val onlyFav by viewModel.onlyFavorites.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::updateQuery,
            label = { Text("Buscar ciudad") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = onlyFav,
                onCheckedChange = { viewModel.toggleOnlyFavorites() }
            )
            Text("Mostrar solo favoritos")
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {

                LazyColumn(Modifier.fillMaxSize()) {
                    items(ciudades) { ciudad ->
                        CiudadItem(
                            ciudad = ciudad,
                            onToggleFavorite = { viewModel.toggleFavorite(ciudad) },
                            onNavigateMap = {  },
                            onOpenInfo = {
                                navController.navigate("cityInfo/${ciudad.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CiudadItem(
    ciudad: CiudadEntity,
    onToggleFavorite: () -> Unit,
    onNavigateMap: () -> Unit,
    onOpenInfo: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("${ciudad.name}, ${ciudad.country}", style = MaterialTheme.typography.titleMedium)
            Text("Lat: ${ciudad.coord.lat}, Lon: ${ciudad.coord.lon}")

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onToggleFavorite) {
                    val icon = if (ciudad.isFavorite) Icons.Outlined.Favorite else Icons.Default.FavoriteBorder
                    Icon(icon, contentDescription = null)
                }

                Button(onClick = onNavigateMap) {
                    Text("Ir al mapa")
                }

                Button(onClick = onOpenInfo) {
                    Text("Info")
                }
            }
        }
    }
}
