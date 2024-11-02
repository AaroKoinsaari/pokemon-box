/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aarokoinsaari.pokemonbox.model.PokemonEntity

@Database(entities = [PokemonEntity::class], version = 1, exportSchema = true)
@TypeConverters(PokemonEntity.Converters::class)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}
