package com.fprieto.hms.wearable.net

import com.fprieto.hms.wearable.model.audiobookshelf.*
import retrofit2.Response
import retrofit2.http.*

interface AudiobookshelfApiService {

    @POST("login") // Assuming base URL will be set in Retrofit client
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @GET("api/libraries")
    suspend fun getLibraries(
        @Header("Authorization") token: String
    ): Response<LibrariesResponse> // Adjusted to expect the wrapper

    @GET("api/libraries/{libraryId}/items")
    suspend fun getLibraryItems(
        @Header("Authorization") token: String,
        @Path("libraryId") libraryId: String,
        @Query("limit") limit: Int? = null,
        @Query("page") page: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("desc") desc: Int? = null, // 0 for false, 1 for true
        @Query("filter") filter: String? = null,
        @Query("minified") minified: Int? = null, // 0 for false, 1 for true
        @Query("collapseseries") collapseseries: Int? = null // 0 for false, 1 for true
    ): Response<LibraryItemsResponse>

    @POST("api/items/{itemId}/play")
    suspend fun playBook(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String,
        @Body deviceInfoRequest: DeviceInfoRequest? = DeviceInfoRequest() // Send empty body or basic device info
    ): Response<PlaybackSessionResponse>

    @POST("api/items/{itemId}/play/{episodeId}")
    suspend fun playPodcastEpisode(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String,
        @Path("episodeId") episodeId: String,
        @Body deviceInfoRequest: DeviceInfoRequest? = DeviceInfoRequest()
    ): Response<PlaybackSessionResponse>

    @POST("api/me/progress")
    suspend fun batchUpdateMediaProgress(
        @Header("Authorization") token: String,
        @Body mediaProgressList: List<MediaProgressUpdate>
    ): Response<Unit> // API returns 200 OK, no specific body mentioned for success

    @PATCH("api/me/progress/{libraryItemId}/{episodeId}")
    suspend fun updatePodcastMediaProgress(
        @Header("Authorization") token: String,
        @Path("libraryItemId") libraryItemId: String,
        @Path("episodeId") episodeId: String,
        @Body mediaProgress: MediaProgressUpdate
    ): Response<Unit> // API returns 200 OK

    @PATCH("api/me/progress/{libraryItemId}")
    suspend fun updateBookMediaProgress(
        @Header("Authorization") token: String,
        @Path("libraryItemId") libraryItemId: String,
        @Body mediaProgress: MediaProgressUpdate
    ): Response<Unit> // API returns 200 OK
}

// Request body for login
data class LoginRequest(
    val username: String,
    val password: String
)

// Request body for play endpoints (optional, can be empty)
data class DeviceInfoRequest(
    val deviceInfo: ClientDeviceInfo = ClientDeviceInfo()
)

data class ClientDeviceInfo(
    val clientVersion: String = "AudiobookshelfWear/0.1" // Example client version
    // Add other fields if needed by server, like deviceId, clientName etc.
)

// Data class for updating media progress
// Only send fields that need to be updated
data class MediaProgressUpdate(
    val libraryItemId: String,
    val episodeId: String? = null,
    val currentTime: Double,
    val duration: Double,
    val progress: Double,
    val isFinished: Boolean,
    val lastUpdate: Long // Milliseconds since epoch
)
