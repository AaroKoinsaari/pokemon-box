/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.network

import me.sargunvohra.lib.pokekotlin.model.Pokemon
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonApiService {
    @GET("pokemon/{name}")
    suspend fun getPokemonByName(@Path("name") name: String): Pokemon
}
