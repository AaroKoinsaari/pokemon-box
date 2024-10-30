/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aarokoinsaari.pokemonbox.model.Pokemon

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemons: List<Pokemon>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pokemon: Pokemon)

    @Query("SELECT * FROM pokemons ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getPokemons(limit: Int, offset: Int): List<Pokemon>

    @Query("SELECT * FROM pokemons WHERE name LIKE '%' || :name || '%' ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getPokemonsByName(name: String, limit: Int, offset: Int): List<Pokemon>
}
