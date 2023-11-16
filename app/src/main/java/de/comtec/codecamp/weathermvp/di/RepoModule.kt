package de.comtec.codecamp.weathermvp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.comtec.codecamp.weathermvp.data.database.WeatherDatabase
import de.comtec.codecamp.weathermvp.data.network.QuotesService
import de.comtec.codecamp.weathermvp.data.network.WeatherService
import de.comtec.codecamp.weathermvp.data.repositories.NetworkRepository
import de.comtec.codecamp.weathermvp.data.repositories.QuotesRepository
import de.comtec.codecamp.weathermvp.data.repositories.SettingsRepository
import de.comtec.codecamp.weathermvp.data.repositories.WeatherRepository
import de.comtec.codecamp.weathermvp.util.dataStore
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    data class RetroFitHolder(val weatherRetrofit: Retrofit, val quotesRetrofit: Retrofit)

    @Provides
    fun providesNetworkRepository(@ApplicationContext context: Context): NetworkRepository {
        return NetworkRepository(context)
    }

    @Provides
    fun providesRetroFit(): RetroFitHolder {

        val weatherRetrofit =
            Retrofit.Builder().baseUrl("https://api.open-meteo.com/v1/").addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            ).build()

        val quotesRetrofit = Retrofit.Builder().baseUrl("https://api.quotable.io/").addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            ).build()

        return RetroFitHolder(weatherRetrofit, quotesRetrofit)
    }

    @Provides
    fun provideWeatherAPI(retroFitHolder: RetroFitHolder): WeatherService {
        return retroFitHolder.weatherRetrofit.create(WeatherService::class.java)
    }

    @Provides
    fun provideQuotesAPI(retroFitHolder: RetroFitHolder): QuotesService {
        return retroFitHolder.quotesRetrofit.create(QuotesService::class.java)
    }

    @Provides
    fun provideWeatherRepo(api: WeatherService, weatherDb: WeatherDatabase): WeatherRepository {
        val weatherDao = weatherDb.weatherDao()
        return WeatherRepository(api, weatherDao)
    }

    @Provides
    fun provideQuotesRepo(api: QuotesService): QuotesRepository {
        return QuotesRepository(api)
    }

    @Provides
    fun providesDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context, WeatherDatabase::class.java, "weatherdatabase"
        ).build()
    }

    @Provides
    fun providesSettingsRepo(@ApplicationContext context: Context): SettingsRepository {
        val dataStore = context.dataStore
        return SettingsRepository(dataStore)
    }


}