/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for the Pokemon API.
 */
interface PokemonApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse

    @GET("pokemon/{name}")
    suspend fun searchPokemonByName(@Path("name") name: String): PokemonDetailResponse
}
