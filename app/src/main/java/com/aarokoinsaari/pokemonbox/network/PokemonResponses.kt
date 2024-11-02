/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.network

import com.google.gson.annotations.SerializedName

/**
 * This is to represent the more stripped down detailed response from the API as we do not
 * need all the data from a single pokemon.
 */
data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val sprites: Sprites,
    val types: List<TypeSlot>
) {
    // the image of the pokemon from the front
    data class Sprites(
        @SerializedName("front_default")
        val frontDefault: String?
    )

    data class TypeSlot(
        val slot: Int,
        val type: Type
    )

    data class Type(
        val name: String,
        val url: String
    )
}

/**
 * This is to get the list of pokemons with basic info.
 */
data class PokemonListResponse(
    val results: List<PokemonBasicInfo>
) {
    data class PokemonBasicInfo(
        val name: String,
        val url: String
    )
}

/**
 * This is to just get the description of a pokemon
 */
data class PokemonSpeciesResponse(
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>
) {
    data class FlavorTextEntry(
        @SerializedName("flavor_text") val flavorText: String,
        val language: Language
    )

    data class Language(
        val name: String
    )
}
