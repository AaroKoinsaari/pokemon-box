/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarokoinsaari.pokemonbox.intent.PokemonListIntent
import com.aarokoinsaari.pokemonbox.repository.PokemonRepository
import com.aarokoinsaari.pokemonbox.state.PokemonListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PokemonListViewModel(private val repository: PokemonRepository) : ViewModel() {
    private val _state = MutableStateFlow(PokemonListState())
    val state: StateFlow<PokemonListState> = _state
    private val pageSize = 20
    private var currentOffset = 0

    // Load first pokemons when initialized
    init {
        handleIntent(PokemonListIntent.LoadInitial)
    }

    fun handleIntent(intent: PokemonListIntent) {
        Log.d("PokemonListViewModel", "handleIntent: $intent")
        when (intent) {
            is PokemonListIntent.LoadInitial -> {
                currentOffset = 0
                loadPokemons(reset = true)
            }
            is PokemonListIntent.LoadNextPage -> {
                currentOffset += pageSize
                loadPokemons()
            }
            is PokemonListIntent.UpdateQuery -> {
                _state.value = _state.value.copy(query = intent.query)
                searchPokemons(intent.query)
            }
            is PokemonListIntent.Search -> searchPokemons(intent.query)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loadPokemons(reset: Boolean = false) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val pokemons = repository.getPokemonList(limit = pageSize, offset = currentOffset)
                val currentList = if (reset) pokemons else _state.value.pokemons + pokemons
                _state.value = _state.value.copy(pokemons = currentList, isLoading = false)
                Log.d("PokemonListViewModel", "loadPokemons, current pokemons: $currentList")
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
                Log.d("PokemonListViewModel", "Error loading pokemons: ${e.message}")
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun searchPokemons(query: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val pokemons = repository.getPokemonByName(query)
                Log.d("PokemonListViewModel", "searchPokemons, pokemons: $pokemons")
                _state.value = _state.value.copy(pokemons = pokemons, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
                Log.d("PokemonListViewModel", "Error searching pokemons: ${e.message}")
            }
        }
    }
}
