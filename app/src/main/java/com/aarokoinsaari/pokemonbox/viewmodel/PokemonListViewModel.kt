/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarokoinsaari.pokemonbox.intent.PokemonListIntent
import com.aarokoinsaari.pokemonbox.model.Pokemon
import com.aarokoinsaari.pokemonbox.model.toPokemon
import com.aarokoinsaari.pokemonbox.network.PokemonApiService
import com.aarokoinsaari.pokemonbox.state.PokemonListState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PokemonListViewModel(private val apiService: PokemonApiService) : ViewModel() {
    private val _state = MutableStateFlow(PokemonListState())
    val state: StateFlow<PokemonListState> = _state
    private val limit = 20
    private var currentOffset = 0

    // Load first pokemons when initialized
    init {
        handleIntent(PokemonListIntent.LoadInitial)
    }

    fun handleIntent(intent: PokemonListIntent) {
//        Log.d("PokemonListViewModel", "handleIntent: $intent")
        when (intent) {
            is PokemonListIntent.LoadInitial -> {
                currentOffset = 0
                loadPokemons(reset = true)
            }
            is PokemonListIntent.LoadNextPage -> {
                currentOffset += limit
                loadPokemons()
            }
            is PokemonListIntent.UpdateQuery -> {
                _state.update { it.copy(query = intent.query) }
                filterPokemons(intent.query)
            }
            is PokemonListIntent.Search -> searchPokemons(intent.query)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loadPokemons(reset: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val pokemons = getPokemonsWithDetails(limit = limit, offset = currentOffset)
                _state.update { currentState ->
                    val currentList = if (reset) pokemons else currentState.pokemons + pokemons
//                    Log.d("PokemonListViewModel", "loadPokemons, current pokemons: $currentList")
                    currentState.copy(pokemons = currentList, isLoading = false)
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
//                Log.d("PokemonListViewModel", "Error loading pokemons: ${e.message}")
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun searchPokemons(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val detailResponse = apiService.getPokemonDetail(query)
                val speciesResponse = apiService.getPokemonSpecies(detailResponse.id)
                val pokemon = detailResponse.toPokemon(speciesResponse)
                _state.update { currentState ->
                    currentState.copy( // Add pokemon to the original list as well if it doesn't exist yet
                        pokemons = if (currentState.pokemons.any { it.id == pokemon.id }) {
                            currentState.pokemons
                        } else {
                            currentState.pokemons + pokemon
                        },
                        filteredPokemons = listOf(pokemon),
                        isLoading = false
                    )
                }
//                Log.d("PokemonListViewModel", "searchPokemons, current pokemons: $pokemon")
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
//                Log.d("PokemonListViewModel", "Error searching pokemons: ${e.message}")
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

    private suspend fun getPokemonsWithDetails(limit: Int, offset: Int): List<Pokemon> {
        val response = apiService.getPokemonList(limit, offset)

        return coroutineScope {
            response.results.map { basicInfo ->
                async {
                    val detailResponse = apiService.getPokemonDetail(basicInfo.name)
//                    Log.d("PokemonListViewModel", "getPokemonsWithDetails, pokemon: $detailResponse")
                    val speciesResponse = apiService.getPokemonSpecies(detailResponse.id)
//                    Log.d("PokemonListViewModel", "getPokemonsWithDetails, species: $speciesResponse")
                    detailResponse.toPokemon(speciesResponse)
                }
            }.awaitAll()
        }
    }
}
