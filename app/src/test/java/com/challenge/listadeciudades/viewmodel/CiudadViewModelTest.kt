package com.challenge.listadeciudades.viewmodel

import app.cash.turbine.test
import com.challenge.listadeciudades.data.local.CiudadEntity
import com.challenge.listadeciudades.data.local.Coord
import com.challenge.listadeciudades.data.model.WikipediaUiState
import com.challenge.listadeciudades.data.remote.JsonDownloader
import com.challenge.listadeciudades.data.remote.model.Thumbnail
import com.challenge.listadeciudades.data.remote.model.WikipediaResponse
import com.challenge.listadeciudades.data.repository.CiudadRepository
import com.challenge.listadeciudades.data.repository.WikipediaRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class CiudadViewModelTest {

    private val repository: CiudadRepository = mock()
    private val jsonDownloader: JsonDownloader = mock()
    private val wikipediaRepository: WikipediaRepository = mock()

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: CiudadViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        whenever(repository.getAll()).thenReturn(flowOf(emptyList()))
        whenever(repository.searchByName(any())).thenReturn(flowOf(emptyList()))

        viewModel = CiudadViewModel(repository, jsonDownloader, wikipediaRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleFavorite should call repository`() = runTest {
        val ciudad = CiudadEntity(1, "Buenos Aires", "AR", Coord(0.0, 0.0), false)

        viewModel.toggleFavorite(ciudad)
        advanceUntilIdle()

        verify(repository).toggleFavorite(1, true)
    }

    @Test
    fun `updateQuery should update searchQuery state`() = runTest {
        viewModel.updateQuery("Cordoba")
        assertEquals("Cordoba", viewModel.searchQuery.value)
    }

    @Test
    fun `toggleOnlyFavorites should toggle the state`() = runTest {
        val initial = viewModel.onlyFavorites.value
        viewModel.toggleOnlyFavorites()
        assertEquals(!initial, viewModel.onlyFavorites.value)
    }

    @Test
    fun `verificarYDescargar should insert ciudades if empty`() = runTest {
        val ciudadesMock = listOf(
            CiudadEntity(1, "Buenos Aires", "AR", Coord(0.0, 0.0))
        )
        val flow = flowOf(25 to emptyList(), 100 to ciudadesMock)

        whenever(repository.contarCiudades()).thenReturn(0)
        whenever(jsonDownloader.descargarCiudades(any())).thenReturn(flow)

        viewModel.verificarYDescargar("https://fake.url")

        advanceUntilIdle()

        verify(repository).limpiarCiudades()
        verify(repository).insertarCiudades(ciudadesMock)
        assertEquals(true, viewModel.isReady.value)
    }

    @Test
    fun `verificarYDescargar should skip download if data exists`() = runTest {
        whenever(repository.contarCiudades()).thenReturn(5)

        viewModel.verificarYDescargar("https://fake.url")

        advanceUntilIdle()

        verify(jsonDownloader, never()).descargarCiudades(any())
        assertEquals(true, viewModel.isReady.value)
    }

    @Test
    fun `loadCityInfo should update wikiUiState with data`() = runTest {
        // GIVEN
        val response = WikipediaResponse(
            title = "Cordoba",
            extract = "Ciudad en Argentina",
            thumbnail = Thumbnail("https://image.url")
        )
        whenever(wikipediaRepository.getCityInfo("Cordoba")).thenReturn(response)

        // WHEN
        viewModel.loadCityInfo("Cordoba")
        advanceUntilIdle()

        // THEN
        val state = viewModel.wikiUiState.value
        assertEquals("Cordoba", state.title)
        assertEquals("Ciudad en Argentina", state.extract)
        assertEquals("https://image.url", state.thumbnailUrl)
        assertFalse(state.isLoading)
        assertFalse(state.isError)
    }

    @Test
    fun `loadCityInfo should handle error`() = runTest {
        whenever(wikipediaRepository.getCityInfo("Inexistente"))
            .thenThrow(RuntimeException("Error"))

        viewModel.loadCityInfo("Inexistente")
        advanceUntilIdle()

        assertTrue(viewModel.wikiUiState.value.isError)
        assertFalse(viewModel.wikiUiState.value.isLoading)
    }

    @Test
    fun `resetWikiState should reset to default`() = runTest {
        viewModel.resetWikiState()
        assertEquals(WikipediaUiState(), viewModel.wikiUiState.value)
    }

    @Test
    fun `ciudades emits all cities when searchQuery is blank and onlyFavorites is false`() = runTest {
        val ciudad1 = CiudadEntity(1, "Cordoba", "AR", Coord(0.0, 0.0), false)
        val ciudad2 = CiudadEntity(2, "Rosario", "AR", Coord(0.0, 0.0), true)
        val ciudades = listOf(ciudad1, ciudad2)

        whenever(repository.getAll()).thenReturn(flowOf(ciudades))
        whenever(repository.searchByName(any())).thenReturn(flowOf(emptyList()))

        val viewModel = CiudadViewModel(repository, jsonDownloader, wikipediaRepository)

        viewModel.ciudades.test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.containsAll(ciudades))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ciudades emits filtered cities when searchQuery is set`() = runTest {
        val ciudad = CiudadEntity(1, "Cordoba", "AR", Coord(0.0, 0.0), false)
        whenever(repository.getAll()).thenReturn(flowOf(emptyList()))
        whenever(repository.searchByName("Cord")).thenReturn(flowOf(listOf(ciudad)))

        val viewModel = CiudadViewModel(repository, jsonDownloader, wikipediaRepository)

        viewModel.updateQuery("Cord")

        viewModel.ciudades.test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Cordoba", result.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ciudades emits only favorites when toggleOnlyFavorites is true`() = runTest {
        val fav = CiudadEntity(1, "Rosario", "AR", Coord(0.0, 0.0), true)
        val nonFav = CiudadEntity(2, "Cordoba", "AR", Coord(0.0, 0.0), false)
        val ciudades = listOf(fav, nonFav)

        whenever(repository.getAll()).thenReturn(flowOf(ciudades))
        whenever(repository.searchByName(any())).thenReturn(flowOf(emptyList()))

        val viewModel = CiudadViewModel(repository, jsonDownloader, wikipediaRepository)

        viewModel.toggleOnlyFavorites()

        viewModel.ciudades.test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertTrue(result.all { it.isFavorite })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ciudades filters by searchQuery and onlyFavorites`() = runTest {
        val ciudad1 = CiudadEntity(1, "Rosario", "AR", Coord(0.0, 0.0), true)
        val ciudad2 = CiudadEntity(2, "Rosario Norte", "AR", Coord(0.0, 0.0), false)

        whenever(repository.getAll()).thenReturn(flowOf(emptyList()))
        whenever(repository.searchByName("Ros")).thenReturn(flowOf(listOf(ciudad1, ciudad2)))

        val viewModel = CiudadViewModel(repository, jsonDownloader, wikipediaRepository)

        viewModel.updateQuery("Ros")
        viewModel.toggleOnlyFavorites()

        viewModel.ciudades.test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals(true, result[0].isFavorite)
            cancelAndIgnoreRemainingEvents()
        }
    }




}
