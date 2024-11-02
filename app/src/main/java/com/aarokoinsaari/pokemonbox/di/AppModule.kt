/*
 * Copyright (c) 2024 Aaro Koinsaari. All rights reserved.
 */

package com.aarokoinsaari.pokemonbox.di

import androidx.room.Room
import com.aarokoinsaari.pokemonbox.data.local.PokemonDatabase
import com.aarokoinsaari.pokemonbox.data.remote.PokemonApiService
import com.aarokoinsaari.pokemonbox.data.repository.PokemonRepository
import com.aarokoinsaari.pokemonbox.viewmodel.PokemonListViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<PokemonApiService> { get<Retrofit>().create(PokemonApiService::class.java) }

    single {
        Room.databaseBuilder(
            androidContext(),
            PokemonDatabase::class.java,
            "pokemon_database"
        ).fallbackToDestructiveMigration().build()
    }

    // For testing
//    single {
//        Room.inMemoryDatabaseBuilder(
//            androidContext(),
//            PokemonDatabase::class.java
//        ).build()
//    }

    single { get<PokemonDatabase>().pokemonDao() }

    single { PokemonRepository(get(), get()) }

    viewModel { PokemonListViewModel(get()) }
}
