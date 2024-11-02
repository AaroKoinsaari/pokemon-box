/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.model

import com.aarokoinsaari.pokemonbox.data.remote.PokemonDetailResponse
import com.aarokoinsaari.pokemonbox.data.remote.PokemonSpeciesResponse

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val types: List<String>?,
    val description: String?
)

fun PokemonDetailResponse.toPokemon(speciesResponse: PokemonSpeciesResponse): Pokemon {
    val description = speciesResponse.flavorTextEntries
        .firstOrNull { it.language.name == "en" }
        ?.flavorText
        ?.replace("\n", " ")

    return Pokemon(
        id = this.id,
        name = this.name,
        imageUrl = this.sprites.frontDefault,
        types = this.types.map { it.type.name },
        description = description
    )
}
