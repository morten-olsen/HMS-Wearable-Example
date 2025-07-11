package com.fprieto.hms.wearable.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadedFile(
    val libraryItemId: String,
    val fileName: String,
    val filePath: String, // Absolute path to the downloaded file
    val downloadTimestamp: Long,
    val fileSize: Long // Size in bytes
    // Could add originalUrl, mimeType etc. if needed
) : Parcelable
