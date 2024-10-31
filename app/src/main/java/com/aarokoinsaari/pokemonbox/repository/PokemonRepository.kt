/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.repository

import com.aarokoinsaari.pokemonbox.data.local.PokemonDao
import com.aarokoinsaari.pokemonbox.model.Pokemon
import com.aarokoinsaari.pokemonbox.network.PokemonApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository class for managing Pokemon data. It fetches data from the local database
 * and remote PokeApi
 */
class PokemonRepository(
    private val pokemonDao: PokemonDao,
    private val pokemonApiService: PokemonApiService
) {
    /**
     * Fetches a list of pokemons from the local database first and then from the API if some not
     * found, and stores new ones locally
     */
    suspend fun getPokemonList(limit: Int, offset: Int): List<Pokemon> = withContext(Dispatchers.IO) {
        val localPokemons = pokemonDao.getPokemons(limit, offset)
        if (localPokemons.isNotEmpty()) {
            localPokemons
        } else { // Fetch from api
            val apiResponse = pokemonApiService.getPokemonList(limit, offset)
            val pokemons = apiResponse.results.map { result ->
                val pokemonDetail = pokemonApiService.searchPokemonByName(result.name)
                Pokemon(
                    id = pokemonDetail.id,
                    name = pokemonDetail.name,
                    imageUrl = pokemonDetail.sprites.frontDefault,
                    types = pokemonDetail.types.map { it.type.name },
                    description = null // TODO
                )
            }
            pokemonDao.insertAll(pokemons)
            pokemons
        }
    }

    /**
     * Searches a pokemon by its name. Does the same thing by first fetching from local db
     * and only after from api.
     */
    suspend fun getPokemonByName(name: String): List<Pokemon> = withContext(Dispatchers.IO) {
        val localPokemons = pokemonDao.getPokemonsByName(name, limit = 20, offset = 0)
        if (localPokemons.isNotEmpty()) {
            localPokemons
        } else {
            val detail = pokemonApiService.searchPokemonByName(name)
            val pokemon = Pokemon(
                id = detail.id,
                name = detail.name,
                imageUrl = detail.sprites.frontDefault,
                types = detail.types.map { it.type.name },
                description = null // TODO
            )
            pokemonDao.insert(pokemon)
            listOf(pokemon)
        }
    }
}
