package com.fprieto.hms.wearable.model.audiobookshelf

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("type") val type: String,
    @SerializedName("token") val token: String,
    @SerializedName("mediaProgress") val mediaProgress: List<MediaProgress>?,
    @SerializedName("seriesHideFromContinueListening") val seriesHideFromContinueListening: List<String>?,
    @SerializedName("bookmarks") val bookmarks: List<AudioBookmark>?,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("isLocked") val isLocked: Boolean,
    @SerializedName("lastSeen") val lastSeen: Long?,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("permissions") val permissions: UserPermissions?,
    @SerializedName("librariesAccessible") val librariesAccessible: List<String>?,
    @SerializedName("itemTagsAccessible") val itemTagsAccessible: List<String>?
) : Parcelable

@Parcelize
data class UserPermissions(
    @SerializedName("download") val download: Boolean,
    @SerializedName("update") val update: Boolean,
    @SerializedName("delete") val delete: Boolean,
    @SerializedName("upload") val upload: Boolean,
    @SerializedName("accessAllLibraries") val accessAllLibraries: Boolean,
    @SerializedName("accessAllTags") val accessAllTags: Boolean,
    @SerializedName("accessExplicitContent") val accessExplicitContent: Boolean
) : Parcelable

// Minimal placeholder, expand if needed
@Parcelize
data class AudioBookmark(
    @SerializedName("libraryItemId") val libraryItemId: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("time") val time: Double?,
    @SerializedName("createdAt") val createdAt: Long?
) : Parcelable
