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
)

// Holds the url to the pokemon's default image (from the front)
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
