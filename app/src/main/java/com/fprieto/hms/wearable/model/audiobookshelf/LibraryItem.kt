package com.fprieto.hms.wearable.model.audiobookshelf

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LibraryItem(
    @SerializedName("id") val id: String,
    @SerializedName("ino") val ino: String?,
    @SerializedName("libraryId") val libraryId: String,
    @SerializedName("folderId") val folderId: String?,
    @SerializedName("path") val path: String?,
    @SerializedName("relPath") val relPath: String?,
    @SerializedName("isFile") val isFile: Boolean?,
    @SerializedName("mtimeMs") val mtimeMs: Long?,
    @SerializedName("ctimeMs") val ctimeMs: Long?,
    @SerializedName("birthtimeMs") val birthtimeMs: Long?,
    @SerializedName("addedAt") val addedAt: Long?,
    @SerializedName("updatedAt") val updatedAt: Long?,
    @SerializedName("isMissing") val isMissing: Boolean?,
    @SerializedName("isInvalid") val isInvalid: Boolean?,
    @SerializedName("mediaType") val mediaType: String, // "book" or "podcast"
    @SerializedName("media") val media: Media,
    // For collapsed series items, this might be present
    @SerializedName("collapsedSeries") val collapsedSeries: CollapsedSeries? = null,
    // For items in a series, this might be present when filtering by series
    // This is a complex one, may need specific handling if series sequence is directly needed.
    // For now, keeping media.metadata.seriesName as the primary series info.
    // @SerializedName("seriesSequence") val seriesSequence: String? // Or a more complex object

    // Simplified from the API doc: numFiles and size are often on the minified version or media object
    @SerializedName("numFiles") val numFiles: Int? = null, // Primarily for minified views
    @SerializedName("size") val size: Long? = null // Primarily for minified views
) : Parcelable

@Parcelize
data class CollapsedSeries(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("nameIgnorePrefix") val nameIgnorePrefix: String?,
    @SerializedName("numBooks") val numBooks: Int
) : Parcelable


// Wrapper for /api/libraries/{libraryId}/items endpoint
@Parcelize
data class LibraryItemsResponse(
    @SerializedName("results") val results: List<LibraryItem>,
    @SerializedName("total") val total: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("sortBy") val sortBy: String?,
    @SerializedName("sortDesc") val sortDesc: Boolean?,
    @SerializedName("filterBy") val filterBy: String?,
    @SerializedName("mediaType") val mediaType: String?,
    @SerializedName("minified") val minified: Boolean?,
    @SerializedName("collapseseries") val collapseseries: Boolean?,
    @SerializedName("include") val include: String?
): Parcelable
