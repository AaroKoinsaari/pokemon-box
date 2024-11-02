/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
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
