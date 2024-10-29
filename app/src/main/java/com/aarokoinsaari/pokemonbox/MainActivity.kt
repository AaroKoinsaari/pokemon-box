/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aarokoinsaari.pokemonbox.ui.screen.PokemonListScreen
import com.aarokoinsaari.pokemonbox.ui.theme.PokemonBoxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokemonBoxTheme {
                PokemonListScreen(
                    stateFlow = TODO()
                )
            }
        }
    }
}
