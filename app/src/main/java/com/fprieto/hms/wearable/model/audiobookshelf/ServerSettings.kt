package com.fprieto.hms.wearable.model.audiobookshelf

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServerSettings(
    @SerializedName("id") val id: String?,
    @SerializedName("scannerFindCovers") val scannerFindCovers: Boolean?,
    @SerializedName("scannerCoverProvider") val scannerCoverProvider: String?,
    @SerializedName("scannerParseSubtitle") val scannerParseSubtitle: Boolean?,
    @SerializedName("scannerPreferMatchedMetadata") val scannerPreferMatchedMetadata: Boolean?,
    @SerializedName("scannerDisableWatcher") val scannerDisableWatcher: Boolean?,
    @SerializedName("storeCoverWithItem") val storeCoverWithItem: Boolean?,
    @SerializedName("storeMetadataWithItem") val storeMetadataWithItem: Boolean?,
    @SerializedName("metadataFileFormat") val metadataFileFormat: String?,
    @SerializedName("rateLimitLoginRequests") val rateLimitLoginRequests: Int?,
    @SerializedName("rateLimitLoginWindow") val rateLimitLoginWindow: Int?,
    @SerializedName("backupSchedule") val backupSchedule: String?, // Can be cron string or boolean false
    @SerializedName("backupsToKeep") val backupsToKeep: Int?,
    @SerializedName("maxBackupSize") val maxBackupSize: Int?,
    @SerializedName("loggerDailyLogsToKeep") val loggerDailyLogsToKeep: Int?,
    @SerializedName("loggerScannerLogsToKeep") val loggerScannerLogsToKeep: Int?,
    @SerializedName("homeBookshelfView") val homeBookshelfView: Int?,
    @SerializedName("bookshelfView") val bookshelfView: Int?,
    @SerializedName("sortingIgnorePrefix") val sortingIgnorePrefix: Boolean?,
    @SerializedName("sortingPrefixes") val sortingPrefixes: List<String>?,
    @SerializedName("chromecastEnabled") val chromecastEnabled: Boolean?,
    @SerializedName("dateFormat") val dateFormat: String?,
    @SerializedName("language") val language: String?,
    @SerializedName("logLevel") val logLevel: Int?,
    @SerializedName("version") val version: String?
) : Parcelable
