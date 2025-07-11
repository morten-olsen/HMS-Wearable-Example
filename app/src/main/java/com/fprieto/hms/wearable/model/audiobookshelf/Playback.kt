package com.fprieto.hms.wearable.model.audiobookshelf

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioTrack(
    @SerializedName("index") val index: Int,
    @SerializedName("startOffset") val startOffset: Double, // seconds
    @SerializedName("duration") val duration: Double,    // seconds
    @SerializedName("title") val title: String?,
    @SerializedName("contentUrl") val contentUrl: String, // This is the crucial URL for streaming/downloading
    @SerializedName("mimeType") val mimeType: String,
    @SerializedName("metadata") val metadata: AudioTrackMetadata?
) : Parcelable

@Parcelize
data class AudioTrackMetadata(
    @SerializedName("filename") val filename: String?,
    @SerializedName("ext") val ext: String?,
    @SerializedName("path") val path: String?,
    @SerializedName("relPath") val relPath: String?,
    @SerializedName("size") val size: Long?,
    @SerializedName("mtimeMs") val mtimeMs: Long?,
    @SerializedName("ctimeMs") val ctimeMs: Long?,
    @SerializedName("birthtimeMs") val birthtimeMs: Long?
) : Parcelable

@Parcelize
data class PlaybackSessionResponse(
    @SerializedName("id") val id: String, // Playback session ID
    @SerializedName("userId") val userId: String,
    @SerializedName("libraryId") val libraryId: String,
    @SerializedName("libraryItemId") val libraryItemId: String,
    @SerializedName("episodeId") val episodeId: String?, // Null for books
    @SerializedName("mediaType") val mediaType: String,
    @SerializedName("mediaMetadata") val mediaMetadata: Metadata?, // Can be BookMetadata or PodcastMetadata
    @SerializedName("chapters") val chapters: List<BookChapter>?, // Usually for books
    @SerializedName("displayTitle") val displayTitle: String?,
    @SerializedName("displayAuthor") val displayAuthor: String?,
    @SerializedName("coverPath") val coverPath: String?,
    @SerializedName("duration") val duration: Double, // Total duration in seconds
    @SerializedName("playMethod") val playMethod: Int?, // 0: Direct Play, 1: Direct Stream, 2: Transcode
    @SerializedName("mediaPlayer") val mediaPlayer: String?,
    @SerializedName("deviceInfo") val deviceInfo: DeviceInfo?,
    @SerializedName("audioTracks") val audioTracks: List<AudioTrack>,
    // Other fields like serverVersion, date, dayOfWeek, timeListening, startTime, currentTime, startedAt, updatedAt
    // libraryItem (full LibraryItem object)
    // For now, focusing on essentials for playback.
) : Parcelable

@Parcelize
data class BookChapter(
    @SerializedName("id") val id: Int,
    @SerializedName("start") val start: Double, // seconds
    @SerializedName("end") val end: Double,     // seconds
    @SerializedName("title") val title: String
) : Parcelable

@Parcelize
data class DeviceInfo(
    @SerializedName("ipAddress") val ipAddress: String?,
    @SerializedName("clientVersion") val clientVersion: String?,
    @SerializedName("serverVersion") val serverVersion: String?,
    // Other device info fields as needed
) : Parcelable
