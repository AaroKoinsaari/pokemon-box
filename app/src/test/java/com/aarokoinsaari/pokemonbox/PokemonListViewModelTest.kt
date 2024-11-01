/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox

import com.aarokoinsaari.pokemonbox.intent.PokemonListIntent
import com.aarokoinsaari.pokemonbox.model.toPokemon
import com.aarokoinsaari.pokemonbox.network.FlavorTextEntry
import com.aarokoinsaari.pokemonbox.network.Language
import com.aarokoinsaari.pokemonbox.network.PokemonApiService
import com.aarokoinsaari.pokemonbox.network.PokemonBasicInfo
import com.aarokoinsaari.pokemonbox.network.PokemonDetailResponse
import com.aarokoinsaari.pokemonbox.network.PokemonListResponse
import com.aarokoinsaari.pokemonbox.network.PokemonSpeciesResponse
import com.aarokoinsaari.pokemonbox.network.Sprites
import com.aarokoinsaari.pokemonbox.network.Type
import com.aarokoinsaari.pokemonbox.network.TypeSlot
import com.aarokoinsaari.pokemonbox.viewmodel.PokemonListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class PokemonListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val bulbasaurDescription = "A strange seed was planted on its back at birth."
    private val ivusaurDescription = "When the bulb on its back grows large, it appears to lose the ability to stand on its hind legs."
    private val charmanderDescription = "Obviously prefers hot places. When it rains, steam is said to spout from the tip of its tail."

    private lateinit var apiService: PokemonApiService
    private lateinit var viewModel: PokemonListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apiService = mock()
        viewModel = PokemonListViewModel(apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load initial pokemons successfully`() = testScope.runTest {
        val mockBasicInfo = PokemonBasicInfo(name = "Bulbasaur", url = "asd")
        val mockPokemonListResponse = PokemonListResponse(results = listOf(mockBasicInfo))
        val mockDetailResponse = PokemonDetailResponse(
            id = 1,
            name = "Bulbasaur",
            sprites = Sprites(frontDefault = "dsa"),
            types = listOf(TypeSlot(slot = 1, type = Type(name = "grass", url = "dsadsa")))
        )
        val mockSpeciesResponse = PokemonSpeciesResponse(
            flavorTextEntries = listOf(
                FlavorTextEntry(
                    flavorText = bulbasaurDescription,
                    language = Language(name = "en")
                )
            )
        )

        `when`(apiService.getPokemonList(anyInt(), anyInt())).thenReturn(mockPokemonListResponse)
        `when`(apiService.getPokemonDetail("Bulbasaur")).thenReturn(mockDetailResponse)
        `when`(apiService.getPokemonSpecies(1)).thenReturn(mockSpeciesResponse)

        viewModel.handleIntent(PokemonListIntent.LoadInitial)
        testDispatcher.scheduler.advanceUntilIdle() // Let coroutines run

        val state = viewModel.state.first()
        val mockPokemon = mockDetailResponse.toPokemon(mockSpeciesResponse)

        Assert.assertFalse(state.isLoading)
        Assert.assertEquals(listOf(mockPokemon), state.pokemons)
    }

    @Test
    fun `load next page successfully`() = testScope.runTest {
        val mockBasicInfoPage1 = PokemonBasicInfo(name = "Bulbasaur", url = "url")
        val mockPokemonListResponsePage1 = PokemonListResponse(results = listOf(mockBasicInfoPage1))
        val mockBasicInfoPage2 = PokemonBasicInfo(name = "Ivysaur", url = "url2")
        val mockPokemonListResponsePage2 = PokemonListResponse(results = listOf(mockBasicInfoPage2))
        val mockDetailResponse1 = PokemonDetailResponse(
            id = 1,
            name = "Bulbasaur",
            sprites = Sprites(frontDefault = "url1"),
            types = listOf(TypeSlot(slot = 1, type = Type(name = "grass", url = "url11")))
        )
        val mockSpeciesResponse1 = PokemonSpeciesResponse(
            flavorTextEntries = listOf(
                FlavorTextEntry(
                    flavorText = bulbasaurDescription,
                    language = Language(name = "en")
                )
            )
        )
        val mockDetailResponse2 = PokemonDetailResponse(
            id = 2,
            name = "Ivysaur",
            sprites = Sprites(frontDefault = "url2"),
            types = listOf(TypeSlot(slot = 1, type = Type(name = "grass", url = "url22")))
        )
        val mockSpeciesResponse2 = PokemonSpeciesResponse(
            flavorTextEntries = listOf(
                FlavorTextEntry(
                    flavorText = ivusaurDescription,
                    language = Language(name = "en")
                )
            )
        )

        `when`(apiService.getPokemonList(20, 0)).thenReturn(mockPokemonListResponsePage1)
        `when`(apiService.getPokemonDetail("Bulbasaur")).thenReturn(mockDetailResponse1)
        `when`(apiService.getPokemonSpecies(1)).thenReturn(mockSpeciesResponse1)
        `when`(apiService.getPokemonList(20, 20)).thenReturn(mockPokemonListResponsePage2)
        `when`(apiService.getPokemonDetail("Ivysaur")).thenReturn(mockDetailResponse2)
        `when`(apiService.getPokemonSpecies(2)).thenReturn(mockSpeciesResponse2)


        viewModel.handleIntent(PokemonListIntent.LoadInitial)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.handleIntent(PokemonListIntent.LoadNextPage)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.first()
        val mockPokemon1 = mockDetailResponse1.toPokemon(mockSpeciesResponse1)
        val mockPokemon2 = mockDetailResponse2.toPokemon(mockSpeciesResponse2)

        Assert.assertFalse(state.isLoading)
        Assert.assertEquals(listOf(mockPokemon1, mockPokemon2), state.pokemons)
    }

    @Test
    fun `search pokemon successfully updates filtered list`() = testScope.runTest {
        val mockDetailResponse = PokemonDetailResponse(
            id = 1,
            name = "Bulbasaur",
            sprites = Sprites(frontDefault = "url"),
            types = listOf(TypeSlot(slot = 1, type = Type(name = "grass", url = "typeUrl")))
        )
        val mockSpeciesResponse = PokemonSpeciesResponse(
            flavorTextEntries = listOf(
                FlavorTextEntry(
                    flavorText = bulbasaurDescription,
                    language = Language(name = "en")
                )
            )
        )
        `when`(apiService.getPokemonDetail("Bulbasaur")).thenReturn(mockDetailResponse)
        `when`(apiService.getPokemonSpecies(1)).thenReturn(mockSpeciesResponse)

        viewModel.handleIntent(PokemonListIntent.Search("Bulbasaur"))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.first()
        val mockPokemon = mockDetailResponse.toPokemon(mockSpeciesResponse)

        Assert.assertFalse(state.isLoading)
        Assert.assertEquals(listOf(mockPokemon), state.filteredPokemons)
    }

    @Test
    fun `filter pokemons by query updates filtered list correctly`() = testScope.runTest {
        val mockBasicInfo1 = PokemonBasicInfo(name = "Bulbasaur", url = "url1")
        val mockBasicInfo2 = PokemonBasicInfo(name = "Ivysaur", url = "url2")
        val mockBasicInfo3 = PokemonBasicInfo(name = "Charmander", url = "url3")
        val mockPokemonListResponse = PokemonListResponse(results = listOf(mockBasicInfo1, mockBasicInfo2, mockBasicInfo3))
        val mockDetailResponse1 = PokemonDetailResponse(
            id = 1,
            name = "Bulbasaur",
            sprites = Sprites(frontDefault = "frontUrl1"),
            types = listOf(TypeSlot(slot = 1, type = Type(name = "grass", url = "typeUrl1")))
        )
        val mockSpeciesResponse1 = PokemonSpeciesResponse(
            flavorTextEntries = listOf(
                FlavorTextEntry(flavorText = bulbasaurDescription, language = Language(name = "en"))
            )
        )

        val mockDetailResponse2 = PokemonDetailResponse(
            id = 2,
            name = "Ivysaur",
            sprites = Sprites(frontDefault = "frontUrl2"),
            types = listOf(TypeSlot(slot = 1, type = Type(name = "grass", url = "typeUrl2")))
        )
        val mockSpeciesResponse2 = PokemonSpeciesResponse(
            flavorTextEntries = listOf(
                FlavorTextEntry(flavorText = ivusaurDescription, language = Language(name = "en"))
            )
        )

        val mockDetailResponse3 = PokemonDetailResponse(
            id = 3,
            name = "Charmander",
            sprites = Sprites(frontDefault = "frontUrl3"),
            types = listOf(TypeSlot(slot = 1, type = Type(name = "fire", url = "typeUrl3")))
        )
        val mockSpeciesResponse3 = PokemonSpeciesResponse(
            flavorTextEntries = listOf(
                FlavorTextEntry(flavorText = charmanderDescription, language = Language(name = "en"))
            )
        )

        `when`(apiService.getPokemonList(anyInt(), anyInt())).thenReturn(mockPokemonListResponse)
        `when`(apiService.getPokemonDetail("Bulbasaur")).thenReturn(mockDetailResponse1)
        `when`(apiService.getPokemonSpecies(1)).thenReturn(mockSpeciesResponse1)
        `when`(apiService.getPokemonDetail("Ivysaur")).thenReturn(mockDetailResponse2)
        `when`(apiService.getPokemonSpecies(2)).thenReturn(mockSpeciesResponse2)
        `when`(apiService.getPokemonDetail("Charmander")).thenReturn(mockDetailResponse3)
        `when`(apiService.getPokemonSpecies(3)).thenReturn(mockSpeciesResponse3)

        viewModel.handleIntent(PokemonListIntent.LoadInitial)
        testDispatcher.scheduler.advanceUntilIdle()

        val mockPokemon1 = mockDetailResponse1.toPokemon(mockSpeciesResponse1)
        val mockPokemon2 = mockDetailResponse2.toPokemon(mockSpeciesResponse2)
        val mockPokemon3 = mockDetailResponse3.toPokemon(mockSpeciesResponse3)
        val initialState = viewModel.state.first()

        Assert.assertFalse(initialState.isLoading)
        Assert.assertEquals(listOf(mockPokemon1, mockPokemon2, mockPokemon3), initialState.pokemons)

        viewModel.handleIntent(PokemonListIntent.UpdateQuery("ivy"))
        testDispatcher.scheduler.advanceUntilIdle()

        val filteredState = viewModel.state.first()
        val expectedFilteredPokemons = listOf(mockPokemon2) // Ivysaur should be first
        Assert.assertEquals(expectedFilteredPokemons, filteredState.filteredPokemons)
    }
}
