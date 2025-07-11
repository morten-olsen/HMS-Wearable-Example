package com.fprieto.hms.wearable.communication

// Enum to define message types
enum class MessageType {
    // Watch to Companion
    REQUEST_LIBRARY_LIST,
    REQUEST_ITEM_SYNC, // User wants to download this item
    REPORT_LISTEN_PROGRESS,

    // Companion to Watch
    SYNC_LIBRARY_LIST_RESPONSE,
    SYNC_ITEM_METADATA, // Sending metadata for a specific item
    SYNC_AUDIO_CHUNK,
    SYNC_AUDIO_COMPLETE,
    SYNC_LISTEN_PROGRESS_UPDATE, // Server-side progress update to watch
    ERROR_MESSAGE
}

// Base class for messages
sealed class WearableMessage(val type: MessageType) {
    // Common fields like timestamp could go here
    val timestamp: Long = System.currentTimeMillis()
}

// --- Watch to Companion Messages ---

data class RequestLibraryList(
    val filter: String? = null // e.g., "podcasts", "audiobooks", or null for all
) : WearableMessage(MessageType.REQUEST_LIBRARY_LIST)

data class RequestItemSync(
    val itemId: String, // ID of the LibraryItem
    val mediaType: String // "audiobook" or "podcastEpisode"
) : WearableMessage(MessageType.REQUEST_ITEM_SYNC)

data class ReportListenProgress(
    val itemId: String,
    val episodeId: String? = null, // For podcasts
    val currentTime: Double,
    val duration: Double,
    val progress: Double,
    val isFinished: Boolean,
    val lastUpdateTimestamp: Long
) : WearableMessage(MessageType.REPORT_LISTEN_PROGRESS)


// --- Companion to Watch Messages ---

// Simplified LibraryItem for sending to watch (subset of original LibraryItem)
data class SyncedLibraryItem(
    val id: String,
    val title: String,
    val author: String?,
    val coverUrl: String?, // Companion might download and send a scaled version
    val mediaType: String, // "audiobook" or "podcast" (series)
    val isDownloaded: Boolean = false // Initial state on watch
)

data class SyncLibraryListResponse(
    val items: List<SyncedLibraryItem>
) : WearableMessage(MessageType.SYNC_LIBRARY_LIST_RESPONSE)

// More detailed metadata for a specific item being synced or viewed
data class SyncItemMetadata(
    val item: SyncedLibraryItem,
    val episodes: List<SyncedEpisodeItem>? = null // For podcasts
    // Potentially add chapters for audiobooks if needed on watch
) : WearableMessage(MessageType.SYNC_ITEM_METADATA)

data class SyncedEpisodeItem(
    val id: String,
    val title: String,
    val duration: Long, // in seconds
    val isDownloaded: Boolean = false
)

data class SyncAudioChunk(
    val itemId: String,
    val episodeId: String? = null, // For podcasts
    val chunkIndex: Int,
    val totalChunks: Int,
    val data: ByteArray, // Actual audio data chunk
    val offset: Long // Offset of this chunk in the overall file
) : WearableMessage(MessageType.SYNC_AUDIO_CHUNK) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SyncAudioChunk
        if (itemId != other.itemId) return false
        if (episodeId != other.episodeId) return false
        if (chunkIndex != other.chunkIndex) return false
        if (totalChunks != other.totalChunks) return false
        if (!data.contentEquals(other.data)) return false
        if (offset != other.offset) return false
        return true
    }

    override fun hashCode(): Int {
        var result = itemId.hashCode()
        result = 31 * result + (episodeId?.hashCode() ?: 0)
        result = 31 * result + chunkIndex
        result = 31 * result + totalChunks
        result = 31 * result + data.contentHashCode()
        result = 31 * result + offset.hashCode()
        return result
    }
}

data class SyncAudioComplete(
    val itemId: String,
    val episodeId: String? = null, // For podcasts
    val success: Boolean,
    val filePathOnWatch: String? = null, // Path where the file is stored on the watch
    val errorMessage: String? = null
) : WearableMessage(MessageType.SYNC_AUDIO_COMPLETE)

data class SyncListenProgressUpdate( // Companion pushing update from server
    val itemId: String,
    val episodeId: String? = null,
    val currentTime: Double,
    val duration: Double,
    val progress: Double,
    val isFinished: Boolean
): WearableMessage(MessageType.SYNC_LISTEN_PROGRESS_UPDATE)

data class ErrorMessage(
    val originalRequestType: MessageType?,
    val message: String
) : WearableMessage(MessageType.ERROR_MESSAGE)
