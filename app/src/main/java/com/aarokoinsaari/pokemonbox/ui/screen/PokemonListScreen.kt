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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
    val state = stateFlow.collectAsState()
    val listState = rememberLazyListState()

    // Launch paging effect when reaching the end of the list
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null &&
                    lastVisibleIndex >= state.value.pokemons.size - 1 &&
                    !state.value.isLoading
                ) {
                    onIntent(PokemonListIntent.LoadNextPage)
                }
            }
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .statusBarsPadding()
        ) {
            SearchTextField(
                query = state.value.query,
                onQueryChange = { onIntent(PokemonListIntent.UpdateQuery(it)) },
                onSearch = { onIntent(PokemonListIntent.Search(state.value.query)) }
            )
            PokemonList(
                // Choose between displaying all pokemons or the filtered one
                pokemons = if (state.value.query.isEmpty()) {
                    state.value.pokemons
                } else {
                    state.value.filteredPokemons
                },
                isLoading = state.value.isLoading,
                listState = listState,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
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
                contentDescription = stringResource(id = R.string.search_icon_content_desc)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledContainerColor = MaterialTheme.colorScheme.secondary,
            focusedContainerColor = MaterialTheme.colorScheme.secondary,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
            cursorColor = MaterialTheme.colorScheme.onSurface,
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onSearch() }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .heightIn(max = 60.dp)
    )
}

@Composable
fun PokemonList(
    pokemons: List<Pokemon>,
    isLoading: Boolean,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
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
            if (isLoading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
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
                fontWeight = FontWeight.Bold,
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
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            // Description
            if (!pokemon.description.isNullOrEmpty()) {
                Text(
                    text = pokemon.description.replaceFirstChar { it.uppercaseChar() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun PokemonTypeLabel(type: String, modifier: Modifier = Modifier) {
    Text(
        text = type.replaceFirstChar { it.uppercase() },
        color = MaterialTheme.colorScheme.onSecondary,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Preview
@Composable
private fun PokemonListScreen_Preview() {
    val pokemons = listOf(
        Pokemon(
            id = 1,
            name = "bulbasaur",
            types = listOf("Electric", "Water"),
            description = "Can float on water",
            imageUrl = null
        ),
        Pokemon(
            id = 2,
            name = "pikachu",
            types = listOf("Electric"),
            description = "is fast",
            imageUrl = null
        )
    )
    PokemonBoxTheme {
        PokemonListScreen(
            stateFlow = MutableStateFlow(
                PokemonListState(
                    pokemons = pokemons
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
