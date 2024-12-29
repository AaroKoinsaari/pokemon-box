/*
 * Copyright (c) 2024 Aaro Koinsaari
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.aarokoinsaari.pokemonbox.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarokoinsaari.pokemonbox.data.repository.PokemonRepository
import com.aarokoinsaari.pokemonbox.intent.PokemonListIntent
import com.aarokoinsaari.pokemonbox.state.PokemonListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Suppress("TooGenericExceptionCaught")
class PokemonListViewModel(private val repository: PokemonRepository) : ViewModel() {
    private val _state = MutableStateFlow(PokemonListState())
    val state: StateFlow<PokemonListState> = _state
    private var currentOffset = 0

    // Load first 20 pokemons when initialized
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
                currentOffset += LIMIT
                loadPokemons()
            }
            is PokemonListIntent.UpdateQuery -> {
                _state.update { it.copy(query = intent.query) }
                filterPokemons(intent.query)
            }
            is PokemonListIntent.Search -> searchPokemonByName(intent.query)
        }
    }

    private fun loadPokemons(reset: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val pokemons = repository.getPokemons(currentOffset)
                _state.update { currentState ->
                    // filter out possible duplicate pokemons
                    val updatedList = if (reset) {
                        pokemons
                    } else {
                        val existingOnes = currentState.pokemons.map { it.id }.toSet()
                        currentState.pokemons + pokemons.filter { it.id !in existingOnes }
                    }
                    Log.d("PokemonListViewModel", "loadPokemons, current pokemons list size: ${updatedList.size}")
                    currentState.copy(pokemons = updatedList, isLoading = false)
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                Log.d("PokemonListViewModel", "Error loading pokemons: ${e.message}")
            }
        }
    }

    private fun searchPokemonByName(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val pokemon = repository.searchPokemonByName(query)
                _state.update { currentState ->
                    currentState.copy( // Add pokemon to the list if not already
                        pokemons = if (currentState.pokemons.any { it.id == pokemon.id }) {
                            currentState.pokemons
                        } else {
                            currentState.pokemons + pokemon
                        },
                        filteredPokemons = listOf(pokemon),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.d("PokemonListViewModel", "Error searching pokemons: ${e.message}")
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun filterPokemons(query: String) {
        val filteredList = if (query.isEmpty()) {
            _state.value.pokemons
        } else {
            _state.value.pokemons.filter { it.name.contains(query, ignoreCase = true) }
        }
        _state.update { it.copy(filteredPokemons = filteredList) }
    }

    companion object {
        private const val LIMIT = 20
    }
}
