/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aarokoinsaari.pokemonbox.R
import com.aarokoinsaari.pokemonbox.intent.PokemonListIntent
import com.aarokoinsaari.pokemonbox.model.Pokemon
import com.aarokoinsaari.pokemonbox.state.PokemonListState
import com.aarokoinsaari.pokemonbox.ui.theme.PokemonBoxTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PokemonListScreen(
    stateFlow: StateFlow<PokemonListState>,
    modifier: Modifier = Modifier,
    onIntent: (PokemonListIntent) -> Unit = { },
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    ) {
        val state = stateFlow.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .statusBarsPadding()
        ) {
            OutlinedTextField(
                value = state.value.query,
                onValueChange = { onIntent(PokemonListIntent.UpdateQuery(it)) },
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_placeholder),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        contentDescription = null // TODO
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .heightIn(max = 60.dp)
            )

            PokemonList(
                pokemons = state.value.pokemons,
                isLoading = state.value.isLoading,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun PokemonList(
    pokemons: List<Pokemon>,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(pokemons) { pokemon ->
                PokemonListItem(
                    pokemon = pokemon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(8.dp)
                )
                HorizontalDivider()
            }
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun PokemonListItem(pokemon: Pokemon, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        // Pokemon image
        AsyncImage(
            model = pokemon.imageUrl,
            contentDescription = pokemon.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
                .width(84.dp)
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            // Pokemon name
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold, // TODO: Should use just MaterialTheme
                modifier = Modifier.padding(bottom = 4.dp)
            )
            // Types
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pokemon.types?.forEach { type ->
                    PokemonTypeLabel(
                        type = type,
                        modifier = Modifier
                            .background(
                                color = Color.LightGray, // TODO: Use MaterialTheme
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            // Description
            Text(
                text = pokemon.description?.replaceFirstChar { it.uppercase() } ?: "",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun PokemonTypeLabel(type: String, modifier: Modifier = Modifier) {
    Text(
        text = type.replaceFirstChar { it.uppercase() },
        color = Color.Gray, // TODO: Use MaterialTheme when adjusted
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

@Preview
@Composable
private fun PokemonListScreen_Preview() {
    val pokemon = Pokemon(
        id = 1,
        name = "bulbasaur",
        types = listOf("Electric", "Water"),
        description = "Can float on water",
        imageUrl = null
    )
    PokemonBoxTheme {
        PokemonListScreen(
            stateFlow = MutableStateFlow(
                PokemonListState(
                    pokemons = listOf(pokemon)
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PokemonListItem_Preview() {
    val pokemon = Pokemon(
        id = 1,
        name = "bulbasaur",
        types = listOf("Electric", "Water"),
        description = "Can float on water",
        imageUrl = null
    )
    PokemonBoxTheme {
        PokemonListItem(pokemon)
    }
}
