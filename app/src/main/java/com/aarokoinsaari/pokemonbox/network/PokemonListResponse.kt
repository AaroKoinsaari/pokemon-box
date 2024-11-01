/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.network

data class PokemonListResponse(
    val results: List<PokemonBasicInfo>
)

data class PokemonBasicInfo(
    val name: String,
    val url: String
)
