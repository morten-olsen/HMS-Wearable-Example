package com.fprieto.hms.wearable.model.audiobookshelf

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaProgress(
    @SerializedName("id") val id: String, // Format: {libraryItemId} or {libraryItemId}-{episodeId}
    @SerializedName("libraryItemId") val libraryItemId: String,
    @SerializedName("episodeId") val episodeId: String?, // Null for books
    @SerializedName("duration") val duration: Double, // Total duration in seconds
    @SerializedName("progress") var progress: Double, // Percentage completion (0.0 to 1.0)
    @SerializedName("currentTime") var currentTime: Double, // Current playback time in seconds
    @SerializedName("isFinished") var isFinished: Boolean,
    @SerializedName("hideFromContinueListening") val hideFromContinueListening: Boolean?,
    @SerializedName("lastUpdate") var lastUpdate: Long?, // Milliseconds since epoch
    @SerializedName("startedAt") val startedAt: Long?, // Milliseconds since epoch
    @SerializedName("finishedAt") var finishedAt: Long? // Milliseconds since epoch, null if not finished
) : Parcelable
