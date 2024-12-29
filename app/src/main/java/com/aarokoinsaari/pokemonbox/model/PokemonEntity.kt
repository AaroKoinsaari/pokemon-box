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

package com.aarokoinsaari.pokemonbox.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "pokemons")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String?,
    val types: String, // Stores as JSON string
    val description: String?
) {
    // Nested class to to handle list conversions
    class Converters {
        @TypeConverter
        fun fromList(value: List<String>?): String =
            Gson().toJson(value)

        @TypeConverter
        fun toList(value: String): List<String>? =
            Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
    }
}

// Convert entity back to Pokemon
fun PokemonEntity.toPokemonModel(): Pokemon =
    Pokemon(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl,
        types = Gson().fromJson<List<String>>(
            types, object : TypeToken<List<String>>() {}.type
        ),
        description = this.description
    )

// Convert Pokemon to PokemonEntity
fun Pokemon.toPokemonEntity(): PokemonEntity =
    PokemonEntity(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl,
        types = Gson().toJson(this.types),
        description = this.description
    )
