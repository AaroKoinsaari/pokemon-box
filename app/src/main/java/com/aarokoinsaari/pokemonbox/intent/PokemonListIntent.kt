/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.intent

sealed class PokemonListIntent {
    object LoadInitial : PokemonListIntent()
    object LoadNextPage : PokemonListIntent()
    data class UpdateQuery(val query: String) : PokemonListIntent()
}
