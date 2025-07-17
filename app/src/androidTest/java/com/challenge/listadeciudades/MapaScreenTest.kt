package com.challenge.listadeciudades

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.challenge.listadeciudades.data.local.CiudadEntity
import com.challenge.listadeciudades.data.local.Coord
import com.challenge.listadeciudades.data.model.CiudadUiState
import com.challenge.listadeciudades.data.model.WikipediaUiState
import com.challenge.listadeciudades.ui.screen.home.HomeScreen
import com.challenge.listadeciudades.viewmodel.CiudadViewModel
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapaScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val mockViewModel = mockk<CiudadViewModel>(relaxed = true)
    private val mockNavController = mockk<NavHostController>(relaxed = true)

    private val ciudad1 = CiudadEntity(
        id = 1,
        name = "Buenos Aires",
        country = "Argentina",
        coord = Coord(lon = -58.417, lat = -34.611),
        isFavorite = false
    )

    private val ciudad2 = CiudadEntity(
        id = 2,
        name = "Córdoba",
        country = "Argentina",
        coord = Coord(lon = -64.1888, lat = -31.4201),
        isFavorite = true
    )

    private val ciudadesFlow = MutableStateFlow(listOf(ciudad1, ciudad2))
    private val queryFlow = MutableStateFlow("")
    private val onlyFavFlow = MutableStateFlow(false)
    private val uiStateFlow = MutableStateFlow(CiudadUiState(isLoading = false))
    private val wikiFlow = MutableStateFlow(WikipediaUiState())

    @Before
    fun setup() {
        every { mockViewModel.ciudades } returns ciudadesFlow
        every { mockViewModel.searchQuery } returns queryFlow
        every { mockViewModel.onlyFavorites } returns onlyFavFlow
        every { mockViewModel.uiState } returns uiStateFlow
        every { mockViewModel.wikiUiState } returns wikiFlow

        every { mockViewModel.updateQuery(any()) } just Runs
        every { mockViewModel.toggleOnlyFavorites() } just Runs
        every { mockViewModel.toggleFavorite(any()) } just Runs
        every { mockViewModel.loadCityInfo(any()) } just Runs
        every { mockViewModel.resetWikiState() } just Runs

//        composeRule.setContent {
//            val navController = rememberNavController()
//            HomeScreen(navController = navController, viewModel = mockViewModel, true)
//        }
    }

    @Test
    fun landscape_cardClick_doesNotCrash() {
        composeRule.setContent {
            HomeScreen(
                navController = mockNavController,
                viewModel = mockViewModel,
                isLandscapeOverride = true // Forzamos landscape
            )
        }

        // Simulamos tap sobre la Card
        composeRule.onNodeWithTag("card_Buenos Aires").performClick()

        // Verificamos que la Card sigue existiendo (no se rompió el árbol de UI)
        composeRule.onNodeWithTag("card_Buenos Aires").assertExists()
        composeRule.onNodeWithTag("MapaScreen").assertExists()
    }
}
