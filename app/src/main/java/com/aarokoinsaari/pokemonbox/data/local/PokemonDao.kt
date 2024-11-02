/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aarokoinsaari.pokemonbox.model.PokemonEntity

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemonList: List<PokemonEntity>)

    @Query("SELECT * FROM pokemons ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPaginatedPokemons(limit: Int, offset: Int): List<PokemonEntity>

    @Query("SELECT * FROM pokemons WHERE name = :name LIMIT 1")
    suspend fun getPokemonByName(name: String): PokemonEntity?

    @Query("SELECT COUNT(*) FROM pokemons")
    suspend fun getPokemonCount(): Int
}
