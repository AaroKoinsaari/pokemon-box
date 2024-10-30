/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.state

import me.sargunvohra.lib.pokekotlin.model.Pokemon

data class PokemonListState(
    val pokemons: List<Pokemon> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false
)
