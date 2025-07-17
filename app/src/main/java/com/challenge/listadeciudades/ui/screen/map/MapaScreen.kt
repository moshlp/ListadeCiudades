package com.challenge.listadeciudades.ui.screen.map


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.challenge.listadeciudades.data.local.CiudadEntity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    selectedCity: CiudadEntity?,
    onBack: () -> Unit
) {
    selectedCity?.let { ciudad ->
        val ciudadLatLng = LatLng(ciudad.coord.lat, ciudad.coord.lon)
        val cameraPositionState = rememberCameraPositionState()

        LaunchedEffect(ciudadLatLng) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(ciudadLatLng, 12f)
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mapa") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = rememberMarkerState(position = ciudadLatLng),
                    title = ciudad.name
                )
            }
        }
    } ?: run {
        // Mostrar algo si no hay ciudad seleccionada
        Text("No hay ciudad seleccionada.")
    }
}
