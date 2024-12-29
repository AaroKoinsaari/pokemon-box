/*
 * Copyright (c) 2024 Aaro Koinsaari
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.aarokoinsaari.pokemonbox

import com.aarokoinsaari.pokemonbox.data.repository.PokemonRepository
import com.aarokoinsaari.pokemonbox.intent.PokemonListIntent
import com.aarokoinsaari.pokemonbox.model.Pokemon
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
@Suppress("MaxLineLength")
/**
 * NOTE: When running these tests, comment all Logs out in the ViewModel or the tests will fail because
 * Mocking Log calls doesn’t work in the JVM test environment. Should probably prefer Mockk but for
 * now the workaround is to just comment them out when testing.
 */
class PokemonListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val bulbasaurDescription = "A strange seed was planted on its back at birth."
    private val ivusaurDescription = "When the bulb on its back grows large, it appears to lose the ability to stand on its hind legs."
    private val charmanderDescription = "Obviously prefers hot places. When it rains, steam is said to spout from the tip of its tail."

    private lateinit var repository: PokemonRepository
    private lateinit var viewModel: PokemonListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = PokemonListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load initial pokemons successfully`() = testScope.runTest {
        val mockPokemon = Pokemon(
            id = 1,
            name = "Bulbasaur",
            imageUrl = "url1",
            types = listOf("grass"),
            description = bulbasaurDescription
        )

        `when`(repository.getPokemons(anyInt())).thenReturn(listOf(mockPokemon))
        viewModel.handleIntent(PokemonListIntent.LoadInitial)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.first()
        Assert.assertFalse(state.isLoading)
        Assert.assertEquals(listOf(mockPokemon), state.pokemons)
    }

    @Test
    fun `load next page successfully`() = testScope.runTest {
        val mockPokemonPage1 = Pokemon(
            id = 1,
            name = "Bulbasaur",
            imageUrl = "url1",
            types = listOf("grass"),
            description = bulbasaurDescription
        )
        val mockPokemonPage2 = Pokemon(
            id = 2,
            name = "Ivysaur",
            imageUrl = "url2",
            types = listOf("grass"),
            description = ivusaurDescription
        )

        `when`(repository.getPokemons(0)).thenReturn(listOf(mockPokemonPage1))
        `when`(repository.getPokemons(20)).thenReturn(listOf(mockPokemonPage2))

        viewModel.handleIntent(PokemonListIntent.LoadInitial)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.handleIntent(PokemonListIntent.LoadNextPage)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.first()
        Assert.assertFalse(state.isLoading)
        Assert.assertEquals(listOf(mockPokemonPage1, mockPokemonPage2), state.pokemons)
    }

    @Test
    fun `search pokemon by name updates filtered list`() = testScope.runTest {
        val mockPokemon = Pokemon(
            id = 1,
            name = "Bulbasaur",
            imageUrl = "url",
            types = listOf("grass"),
            description = bulbasaurDescription
        )

        `when`(repository.searchPokemonByName("Bulbasaur")).thenReturn(mockPokemon)

        viewModel.handleIntent(PokemonListIntent.Search("Bulbasaur"))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.first()
        Assert.assertFalse(state.isLoading)
        Assert.assertEquals(listOf(mockPokemon), state.filteredPokemons)
    }

    @Test
    fun `filter pokemons by query updates filtered list correctly`() = testScope.runTest {
        val mockPokemon1 = Pokemon(
            id = 1,
            name = "Bulbasaur",
            imageUrl = "url1",
            types = listOf("grass"),
            description = bulbasaurDescription
        )
        val mockPokemon2 = Pokemon(
            id = 2,
            name = "Ivysaur",
            imageUrl = "url2",
            types = listOf("grass"),
            description = ivusaurDescription
        )
        val mockPokemon3 = Pokemon(
            id = 3,
            name = "Charmander",
            imageUrl = "url3",
            types = listOf("fire"),
            description = charmanderDescription
        )

        `when`(repository.getPokemons(anyInt())).thenReturn(listOf(mockPokemon1, mockPokemon2, mockPokemon3))


        viewModel.handleIntent(PokemonListIntent.LoadInitial)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.handleIntent(PokemonListIntent.UpdateQuery("Ivy"))
        testDispatcher.scheduler.advanceUntilIdle()

        val filteredState = viewModel.state.first()
        Assert.assertEquals(listOf(mockPokemon2), filteredState.filteredPokemons)
    }
}
