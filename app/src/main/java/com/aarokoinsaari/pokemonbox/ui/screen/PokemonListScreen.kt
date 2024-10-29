/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aarokoinsaari.pokemonbox.intent.PokemonListIntent
import com.aarokoinsaari.pokemonbox.state.PokemonListState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PokemonListScreen(
    stateFlow: StateFlow<PokemonListState>,
    modifier: Modifier = Modifier,
    onIntent: (PokemonListIntent) -> Unit = { }
) {
    // TODO
}
