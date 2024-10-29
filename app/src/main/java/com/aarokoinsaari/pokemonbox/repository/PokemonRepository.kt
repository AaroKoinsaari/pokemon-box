/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.repository

import com.aarokoinsaari.pokemonbox.network.PokemonApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient
import me.sargunvohra.lib.pokekotlin.model.Pokemon

class PokemonRepository(
    private val pokeApi: PokeApiClient,
    private val pokemonApiService: PokemonApiService
) {
    suspend fun getPokemonList(limit: Int = 20, offset: Int = 20): List<Pokemon> = withContext(Dispatchers.IO) {
        pokeApi.getPokemonList(offset, limit).results.map { pokeApi.getPokemon(it.id) }
    }

    suspend fun getPokemonByName(name: String): Pokemon = withContext(Dispatchers.IO) {
        pokemonApiService.getPokemonByName(name.lowercase())
    }
}
