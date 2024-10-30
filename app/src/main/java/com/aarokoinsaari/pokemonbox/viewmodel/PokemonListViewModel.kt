/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.viewmodel

import androidx.lifecycle.ViewModel
import com.aarokoinsaari.pokemonbox.intent.PokemonListIntent
import com.aarokoinsaari.pokemonbox.repository.PokemonRepository
import com.aarokoinsaari.pokemonbox.state.PokemonListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PokemonListViewModel(private val repository: PokemonRepository) : ViewModel() {
    private val _state = MutableStateFlow(PokemonListState())
    val state: StateFlow<PokemonListState> = _state

    init {
        handleIntent(PokemonListIntent.LoadInitial)
    }

    fun handleIntent(intent: PokemonListIntent) {
        when (intent) {
            is PokemonListIntent.LoadInitial -> TODO()
            is PokemonListIntent.LoadNextPage -> TODO()
            is PokemonListIntent.UpdateQuery -> TODO()
            is PokemonListIntent.Search -> TODO()
        }
    }
}
