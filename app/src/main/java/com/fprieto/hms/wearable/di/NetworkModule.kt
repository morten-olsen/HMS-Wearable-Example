package com.fprieto.hms.wearable.di

import android.content.Context
import android.content.SharedPreferences
import com.fprieto.hms.wearable.BuildConfig
import com.fprieto.hms.wearable.net.AudiobookshelfApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// Preference key for storing the base URL
const val PREF_KEY_BASE_URL = "audiobookshelf_base_url"
const val DEFAULT_BASE_URL = "http://10.0.2.2:13378/" // Default for Android emulator to host machine

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("audiobookshelf_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(sharedPreferences: SharedPreferences): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        // Simple interceptor to dynamically set base URL (though Retrofit's baseUrl is usually static)
        // For a truly dynamic base URL per request, other strategies might be needed,
        // but for now, we assume it's set once or changes infrequently.
        // A better approach for dynamic URLs if truly needed per call would be to pass full URLs to Retrofit methods.
        // This setup primarily allows changing the base URL for the whole app via SharedPreferences.
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                var request: Request = chain.request()
                // This is a simplified way to handle dynamic base URL for all requests.
                // Retrofit itself doesn't easily support changing base URL after creation.
                // This interceptor can modify the URL if needed, but it's more for global changes.
                // The AudiobookshelfApiService methods will use relative paths.
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson, sharedPreferences: SharedPreferences): Retrofit {
        val baseUrl = sharedPreferences.getString(PREF_KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideAudiobookshelfApiService(retrofit: Retrofit): AudiobookshelfApiService =
        retrofit.create(AudiobookshelfApiService::class.java)
}
