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

package com.aarokoinsaari.pokemonbox.data.remote

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
