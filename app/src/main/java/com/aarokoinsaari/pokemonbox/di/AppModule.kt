package com.aarokoinsaari.pokemonbox.di

import com.aarokoinsaari.pokemonbox.network.PokemonApiService
import com.aarokoinsaari.pokemonbox.repository.PokemonRepository
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient
import okhttp3.OkHttpClient
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

    single { PokeApiClient() }

    single<PokemonApiService> {
        get<Retrofit>().create(PokemonApiService::class.java)
    }
    single { PokemonRepository(get(), get()) }
}
