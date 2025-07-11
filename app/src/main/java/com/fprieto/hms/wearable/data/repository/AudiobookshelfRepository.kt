package com.fprieto.hms.wearable.data.repository

import com.fprieto.hms.wearable.model.audiobookshelf.*
import com.fprieto.hms.wearable.net.AudiobookshelfApiService
import com.fprieto.hms.wearable.net.LoginRequest
import com.fprieto.hms.wearable.net.MediaProgressUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface AudiobookshelfRepository {
    suspend fun login(serverUrl: String, loginRequest: LoginRequest): Result<LoginResponse>
    suspend fun getLibraries(token: String): Result<LibrariesResponse>
    suspend fun getLibraryItems(
        token: String,
        libraryId: String,
        limit: Int? = null,
        page: Int? = null
    ): Result<LibraryItemsResponse>

    suspend fun playBook(token: String, itemId: String): Result<PlaybackSessionResponse>
    suspend fun playPodcastEpisode(token: String, itemId: String, episodeId: String): Result<PlaybackSessionResponse>
    suspend fun updateMediaProgress(token: String, progressUpdate: MediaProgressUpdate): Result<Unit>
}

@Singleton
class AudiobookshelfRepositoryImpl @Inject constructor(
    private val apiService: AudiobookshelfApiService
    // We might need a way to update Retrofit's base URL if it's not fixed.
    // For now, assuming it's configured once during NetworkModule setup.
) : AudiobookshelfRepository {

    override suspend fun login(serverUrl: String, loginRequest: LoginRequest): Result<LoginResponse> {
        // This is tricky if the base URL for Retrofit is static.
        // For a real dynamic server URL per login, the ApiService or Retrofit client needs to be recreated.
        // For now, we assume the NetworkModule has set the correct base URL from SharedPreferences.
        // If this 'serverUrl' param is different, this won't work as expected without more complex setup.
        // A simple workaround if Retrofit's BaseUrl is fixed: ignore 'serverUrl' here and rely on Dagger providing
        // a pre-configured Retrofit instance. The user would need to set the server URL before login elsewhere.
        return try {
            val response = apiService.login(loginRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Login failed: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLibraries(token: String): Result<LibrariesResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getLibraries("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get libraries: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLibraryItems(
        token: String,
        libraryId: String,
        limit: Int?,
        page: Int?
    ): Result<LibraryItemsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getLibraryItems(
                token = "Bearer $token",
                libraryId = libraryId,
                limit = limit,
                page = page
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get library items: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun playBook(token: String, itemId: String): Result<PlaybackSessionResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.playBook(token = "Bearer $token", itemId = itemId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to play book: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun playPodcastEpisode(
        token: String,
        itemId: String,
        episodeId: String
    ): Result<PlaybackSessionResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.playPodcastEpisode(token = "Bearer $token", itemId = itemId, episodeId = episodeId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to play podcast episode: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMediaProgress(token: String, progressUpdate: MediaProgressUpdate): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = if (progressUpdate.episodeId != null) {
                apiService.updatePodcastMediaProgress(
                    token = "Bearer $token",
                    libraryItemId = progressUpdate.libraryItemId,
                    episodeId = progressUpdate.episodeId,
                    mediaProgress = progressUpdate
                )
            } else {
                apiService.updateBookMediaProgress(
                    token = "Bearer $token",
                    libraryItemId = progressUpdate.libraryItemId,
                    mediaProgress = progressUpdate
                )
            }
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update media progress: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
