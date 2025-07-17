package com.challenge.listadeciudades.ui.screen.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.challenge.listadeciudades.data.local.CiudadEntity

@Composable
fun BuscadorPanel(
    ciudades: List<CiudadEntity>,
    query: String,
    onlyFav: Boolean,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onToggleOnlyFavorites: () -> Unit,
    onToggleFavorite: (CiudadEntity) -> Unit,
    onNavigateMap: (CiudadEntity) -> Unit,
    onOpenInfo: (CiudadEntity) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Buscar ciudad") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = onlyFav,
                onCheckedChange = { onToggleOnlyFavorites() }
            )
            Text("Mostrar solo favoritos")
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(ciudades) { ciudad ->
                        CiudadItem(
                            ciudad = ciudad,
                            onToggleFavorite = { onToggleFavorite(ciudad) },
                            onNavigateMap = { onNavigateMap(ciudad) },
                            onOpenInfo = { onOpenInfo(ciudad) }
                        )
                    }
                }
            }
        }
    }
}
