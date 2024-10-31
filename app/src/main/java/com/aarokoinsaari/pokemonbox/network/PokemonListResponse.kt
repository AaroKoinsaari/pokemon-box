/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.network

data class PokemonListResponse(
    val results: List<PokemonResult>
)

data class PokemonResult(
    val name: String,
    val url: String
)
