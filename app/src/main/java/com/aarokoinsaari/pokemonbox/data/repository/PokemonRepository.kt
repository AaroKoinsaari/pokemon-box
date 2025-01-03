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

package com.aarokoinsaari.pokemonbox.data.repository

import android.util.Log
import com.aarokoinsaari.pokemonbox.data.local.PokemonDao
import com.aarokoinsaari.pokemonbox.data.remote.PokemonApiService
import com.aarokoinsaari.pokemonbox.model.Pokemon
import com.aarokoinsaari.pokemonbox.model.toPokemon
import com.aarokoinsaari.pokemonbox.model.toPokemonEntity
import com.aarokoinsaari.pokemonbox.model.toPokemonModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

@Suppress("TooGenericExceptionCaught")
class PokemonRepository(
    private val pokemonDao: PokemonDao,
    private val apiService: PokemonApiService
) {
    // Fetch paginated pokemons from Room first and if not available fetch rest from API
    suspend fun getPokemons(offset: Int): List<Pokemon> = coroutineScope {
        try {
            val localPokemons = pokemonDao.getPaginatedPokemons(LIMIT, offset)
            Log.d("PokemonRepository", "getPokemons, local pokemons: ${localPokemons.size}")
            if (localPokemons.size == LIMIT) {
                return@coroutineScope localPokemons.map { it.toPokemonModel() }
            } else { // Calculate missing pokemons from page limit and fetch them from the API
                val missingCount = LIMIT - localPokemons.size
                val apiData = fetchPokemonsFromApi(missingCount, offset + localPokemons.size)
                val combinedData = localPokemons.map { it.toPokemonModel() } + apiData
                Log.d("PokemonRepository", "getPokemons, missing count: $missingCount")
                Log.d("PokemonRepository", "getPokemons, fetched pokemons from Api: ${apiData.size}")

                if (pokemonDao.getPokemonCount() < MAX_POKEMONS) {
                    insertPokemonsToDatabase(apiData)
                }
                combinedData.take(LIMIT)
            }
        } catch (e: Exception) {
            Log.d("PokemonRepository", "Error fetching pokemons: ${e.message}")
            emptyList()
        }
    }

    suspend fun searchPokemonByName(query: String): Pokemon {
        val localPokemon = pokemonDao.getPokemonByName(query)
        Log.d("PokemonRepository", "searchPokemonByName, local pokemon: $localPokemon")
        return localPokemon?.toPokemonModel() ?: fetchAndSavePokemon(query)
    }

    private suspend fun fetchAndSavePokemon(name: String): Pokemon {
        val detailResponse = apiService.getPokemonDetail(name)
        val speciesResponse = apiService.getPokemonSpecies(detailResponse.id)
        val pokemon = detailResponse.toPokemon(speciesResponse)
        Log.d("PokemonRepository", "fetchAndSavePokemon, fetched pokemon: $pokemon")
        pokemonDao.insertAll(listOf(pokemon.toPokemonEntity()))
        return pokemon
    }

    private suspend fun fetchPokemonsFromApi(pokemonsToFetch: Int, offset: Int): List<Pokemon> =
        withContext(Dispatchers.IO) {
            apiService.getPokemonList(pokemonsToFetch, offset).results.map { apiPokemon ->
                async {
                    try {
                        val detailResponse = apiService.getPokemonDetail(apiPokemon.name)
                        val speciesResponse = apiService.getPokemonSpecies(detailResponse.id)
                        detailResponse.toPokemon(speciesResponse)
                    } catch (e: Exception) {
                        Log.d("PokemonRepository", "Error fetching pokemon: ${e.message}")
                        null
                    }
                }
            }.awaitAll().filterNotNull()
        }

    private suspend fun insertPokemonsToDatabase(pokemons: List<Pokemon>) {
        val pokemonCountInDb = pokemonDao.getPokemonCount()
        Log.d("PokemonRepository", "insertPokemonsToDatabase, pokemon count in db: $pokemonCountInDb")
        if (pokemonCountInDb < MAX_POKEMONS) {
            val pokemonsToInsert = pokemons.take(MAX_POKEMONS - pokemonCountInDb)
                .map { it.toPokemonEntity() }
            Log.d("PokemonRepository", "insertPokemonsToDatabase, pokemons to insert: $pokemonsToInsert")
            pokemonDao.insertAll(pokemonsToInsert)
        }
    }

    companion object {
        const val LIMIT = 20 // Page size
        const val MAX_POKEMONS = 200  // Max pokemons to be saved in Room
    }
}
