package com.challenge.listadeciudades.ui.screen.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.challenge.listadeciudades.data.local.CiudadEntity

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
            .clickable { onNavigateMap() }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("${ciudad.name}, ${ciudad.country}", style = MaterialTheme.typography.titleMedium)
            Text("Lat: ${ciudad.coord.lat}, Lon: ${ciudad.coord.lon}")

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onToggleFavorite) {
                    val icon =
                        if (ciudad.isFavorite) Icons.Outlined.Favorite else Icons.Default.FavoriteBorder
                    Icon(icon, contentDescription = null)
                }
                Button(onClick = onOpenInfo) {
                    Text("Info")
                }
            }
        }
    }
}