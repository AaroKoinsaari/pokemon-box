/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.state

import com.aarokoinsaari.pokemonbox.model.Pokemon

data class PokemonListState(
    val pokemons: List<Pokemon> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false
)
