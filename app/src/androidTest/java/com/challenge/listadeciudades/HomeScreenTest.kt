package com.challenge.listadeciudades

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

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

        composeRule.setContent {
            val navController = rememberNavController()
            HomeScreen(navController = navController, viewModel = mockViewModel)
        }
    }

    @Test
    fun cities_areDisplayed() {
        composeRule.onNodeWithText("Buenos Aires, Argentina").assertIsDisplayed()
        composeRule.onNodeWithText("Córdoba, Argentina").assertIsDisplayed()
    }

    @Test
    fun cities_areDisplayed_andPopupAppears_onClick() {
        // Verifica que la ciudad esté visible
        composeRule.onNodeWithText("Buenos Aires, Argentina").assertIsDisplayed()

        // Hace clic en el botón Info (dispara showInfoPopup = true)
        composeRule.onNodeWithTag("info_Buenos Aires").performClick()

        // Simula que llega la info desde el ViewModel
        composeRule.runOnIdle {
            wikiFlow.value = WikipediaUiState(
                title = "Buenos Aires",
                extract = "Capital de Argentina",
                thumbnailUrl = null
            )
        }

        // Espera a que aparezca el extracto
        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText("Capital de Argentina").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeRule.onNodeWithText("Capital de Argentina").assertIsDisplayed()
    }

    @Test
    fun infoButton_triggersLoadCityInfo() {
        composeRule.onNodeWithTag("info_Córdoba").performClick()

        verify { mockViewModel.loadCityInfo("Córdoba") }
    }

    @Test
    fun favoriteButton_triggersToggleFavorite() {
        composeRule.onNodeWithTag("fav_Buenos Aires").performClick()

        verify { mockViewModel.toggleFavorite(ciudad1) }
    }

    @Test
    fun toggleOnlyFavorites_checkboxCallsViewModel() {
        composeRule.onNodeWithTag("checkbox_onlyFavorites").performClick()

        verify { mockViewModel.toggleOnlyFavorites() }
    }

    @Test
    fun updateQuery_triggersViewModelCall() {
        composeRule.onNodeWithTag("textfield_search")
            .performTextInput("Rosario")

        verify { mockViewModel.updateQuery("Rosario") }
    }

    @Test
    fun cardClick_navigatesToMap_portrait() {
        composeRule.onNodeWithTag("card_Buenos Aires").performClick()

        verify {
            mockNavController.navigate("map/Buenos Aires/-34.611/-58.417")
        }
    }
}
