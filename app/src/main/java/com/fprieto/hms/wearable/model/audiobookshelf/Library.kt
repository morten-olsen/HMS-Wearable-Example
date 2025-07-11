package com.fprieto.hms.wearable.model.audiobookshelf

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Library(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("folders") val folders: List<Folder>?,
    @SerializedName("displayOrder") val displayOrder: Int?,
    @SerializedName("icon") val icon: String?,
    @SerializedName("mediaType") val mediaType: String, // "book" or "podcast"
    @SerializedName("provider") val provider: String?,
    @SerializedName("settings") val settings: LibrarySettings?,
    @SerializedName("createdAt") val createdAt: Long?,
    @SerializedName("lastUpdate") val lastUpdate: Long?
) : Parcelable

@Parcelize
data class Folder(
    @SerializedName("id") val id: String,
    @SerializedName("fullPath") val fullPath: String,
    @SerializedName("libraryId") val libraryId: String,
    @SerializedName("addedAt") val addedAt: Long?
) : Parcelable

@Parcelize
data class LibrarySettings(
    @SerializedName("coverAspectRatio") val coverAspectRatio: Int?,
    @SerializedName("disableWatcher") val disableWatcher: Boolean?,
    @SerializedName("skipMatchingMediaWithAsin") val skipMatchingMediaWithAsin: Boolean?,
    @SerializedName("skipMatchingMediaWithIsbn") val skipMatchingMediaWithIsbn: Boolean?,
    @SerializedName("autoScanCronExpression") val autoScanCronExpression: String?
) : Parcelable

// Wrapper class for the /api/libraries endpoint
@Parcelize
data class LibrariesResponse(
    @SerializedName("libraries") val libraries: List<Library>
) : Parcelable
