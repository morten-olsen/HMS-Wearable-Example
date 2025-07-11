package com.fprieto.hms.wearable.model.audiobookshelf

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Media(
    // Common fields for Book and Podcast media
    @SerializedName("metadata") val metadata: Metadata?, // Can be BookMetadata or PodcastMetadata
    @SerializedName("coverPath") val coverPath: String?,
    @SerializedName("tags") val tags: List<String>?,

    // Book specific fields (will be null for podcasts)
    @SerializedName("numTracks") val numTracks: Int?,
    @SerializedName("numAudioFiles") val numAudioFiles: Int?,
    @SerializedName("numChapters") val numChapters: Int?,
    @SerializedName("duration") val duration: Double?, // Duration in seconds
    @SerializedName("size") val size: Long?, // Size in bytes
    @SerializedName("ebookFileFormat") val ebookFileFormat: String?,

    // Podcast specific fields (will be null for books)
    @SerializedName("numEpisodes") val numEpisodes: Int?,
    @SerializedName("autoDownloadEpisodes") val autoDownloadEpisodes: Boolean?,
    @SerializedName("autoDownloadSchedule") val autoDownloadSchedule: String?,
    @SerializedName("lastEpisodeCheck") val lastEpisodeCheck: Long?,
    @SerializedName("maxEpisodesToKeep") val maxEpisodesToKeep: Int?,
    @SerializedName("maxNewEpisodesToDownload") val maxNewEpisodesToDownload: Int?

    // Note: The API docs show different structures for Book and Podcast under 'media'.
    // This unified 'Media' class tries to accommodate both for simplicity in LibraryItem.
    // We might need to deserialize to a more generic Map or use a custom TypeAdapter
    // if Gson struggles with the polymorphism of 'metadata' directly.
    // For now, 'metadata' is a generic placeholder.
) : Parcelable

interface Metadata : Parcelable

@Parcelize
data class BookMetadata(
    @SerializedName("title") val title: String?,
    @SerializedName("titleIgnorePrefix") val titleIgnorePrefix: String?,
    @SerializedName("subtitle") val subtitle: String?,
    @SerializedName("authorName") val authorName: String?, // Simplified from authors array
    @SerializedName("narratorName") val narratorName: String?, // Simplified from narrators array
    @SerializedName("seriesName") val seriesName: String?, // Simplified from series array
    @SerializedName("genres") val genres: List<String>?,
    @SerializedName("publishedYear") val publishedYear: String?,
    @SerializedName("publishedDate") val publishedDate: String?, // Can be null
    @SerializedName("publisher") val publisher: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("isbn") val isbn: String?,
    @SerializedName("asin") val asin: String?,
    @SerializedName("language") val language: String?,
    @SerializedName("explicit") val explicit: Boolean?
) : Metadata

@Parcelize
data class PodcastMetadata(
    @SerializedName("title") val title: String?,
    @SerializedName("titleIgnorePrefix") val titleIgnorePrefix: String?,
    @SerializedName("author") val author: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("releaseDate") val releaseDate: String?, // Can be ISO date string
    @SerializedName("genres") val genres: List<String>?,
    @SerializedName("feedUrl") val feedUrl: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("itunesPageUrl") val itunesPageUrl: String?,
    @SerializedName("itunesId") val itunesId: Int?,
    @SerializedName("itunesArtistId") val itunesArtistId: Int?,
    @SerializedName("explicit") val explicit: Boolean?,
    @SerializedName("language") val language: String?,
    @SerializedName("type") val type: String? // e.g., "episodic"
) : Metadata
