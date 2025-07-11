package com.fprieto.hms.wearable.model.audiobookshelf

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
    @SerializedName("user") val user: User,
    @SerializedName("userDefaultLibraryId") val userDefaultLibraryId: String?,
    @SerializedName("serverSettings") val serverSettings: ServerSettings,
    @SerializedName("Source") val source: String? // Note: API shows "Source" with capital S
) : Parcelable
