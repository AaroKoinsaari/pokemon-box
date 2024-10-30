/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.aarokoinsaari.pokemonbox.ui.screen.PokemonListScreen
import com.aarokoinsaari.pokemonbox.ui.theme.PokemonBoxTheme
import com.aarokoinsaari.pokemonbox.viewmodel.PokemonListViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokemonBoxTheme {
                val viewModel: PokemonListViewModel = koinViewModel()
                PokemonListScreen(
                    stateFlow = viewModel.state,
                    onIntent = { viewModel.handleIntent(it) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
