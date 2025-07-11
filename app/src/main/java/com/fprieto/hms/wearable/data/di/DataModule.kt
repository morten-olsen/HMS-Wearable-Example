package com.fprieto.hms.wearable.data.di

import com.fprieto.hms.wearable.data.DeviceLocalSource
import com.fprieto.hms.wearable.data.DeviceLocalSourceImpl
import com.fprieto.hms.wearable.data.repository.AudiobookshelfRepository
import com.fprieto.hms.wearable.data.repository.AudiobookshelfRepositoryImpl
import com.fprieto.hms.wearable.data.repository.DeviceRepository
import android.content.SharedPreferences
import com.fprieto.hms.wearable.data.DeviceLocalSourceImpl
import com.fprieto.hms.wearable.data.DownloadManager
import com.fprieto.hms.wearable.data.repository.AudiobookshelfRepository
import com.fprieto.hms.wearable.data.repository.AudiobookshelfRepositoryImpl
import com.fprieto.hms.wearable.data.repository.DeviceRepository
import com.fprieto.hms.wearable.data.repository.DeviceRepositoryImpl
import com.fprieto.hms.wearable.net.AudiobookshelfApiService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Singleton

@Module
class DataModule {
    @Provides
    @Reusable
    fun provideDeviceRepository(
        deviceLocalSource: DeviceLocalSource
    ): DeviceRepository = DeviceRepositoryImpl(deviceLocalSource)

    @Provides
    @Singleton
    fun provideDeviceLocalSource(): DeviceLocalSource = DeviceLocalSourceImpl

    @Provides
    @Singleton
    fun provideAudiobookshelfRepository(
        apiService: AudiobookshelfApiService
    ): AudiobookshelfRepository = AudiobookshelfRepositoryImpl(apiService)

    @Provides
    @Singleton
    fun provideDownloadManager(
        sharedPreferences: SharedPreferences,
        gson: Gson
    ): DownloadManager = DownloadManager(sharedPreferences, gson)
}
