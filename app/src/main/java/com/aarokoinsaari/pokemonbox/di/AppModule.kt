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
