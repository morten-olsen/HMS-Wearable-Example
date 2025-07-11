package com.fprieto.hms.wearable.data

import android.content.SharedPreferences
import com.fprieto.hms.wearable.model.local.DownloadedFile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

const val PREF_KEY_DOWNLOADED_FILES = "downloaded_files_list"

@Singleton
class DownloadManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {

    fun addDownloadedFile(fileInfo: DownloadedFile) {
        val currentDownloads = getDownloadedFiles().toMutableList()
        // Remove if already exists to update it
        currentDownloads.removeAll { it.libraryItemId == fileInfo.libraryItemId && it.fileName == fileInfo.fileName }
        currentDownloads.add(fileInfo)
        saveDownloadedFiles(currentDownloads)
    }

    fun getDownloadedFile(libraryItemId: String, fileName: String): DownloadedFile? {
        return getDownloadedFiles().firstOrNull { it.libraryItemId == libraryItemId && it.fileName == fileName }
    }

    fun getDownloadedFileForItemId(libraryItemId: String): DownloadedFile? {
        // This might need refinement if an item can have multiple files (e.g. podcast episodes)
        // For now, assumes one primary file per library item for simplicity in playback
        return getDownloadedFiles().firstOrNull { it.libraryItemId == libraryItemId }
    }

    fun getAllDownloadedFilesForItemId(libraryItemId: String): List<DownloadedFile> {
        return getDownloadedFiles().filter { it.libraryItemId == libraryItemId }
    }

    fun getDownloadedFiles(): List<DownloadedFile> {
        val json = sharedPreferences.getString(PREF_KEY_DOWNLOADED_FILES, null)
        return if (json != null) {
            val type = object : TypeToken<List<DownloadedFile>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun removeDownloadedFile(libraryItemId: String, fileName: String) {
        val currentDownloads = getDownloadedFiles().toMutableList()
        val removed = currentDownloads.removeAll { it.libraryItemId == libraryItemId && it.fileName == fileName }
        if (removed) {
            saveDownloadedFiles(currentDownloads)
        }
    }

    fun removeAllForItemId(libraryItemId: String) {
        val currentDownloads = getDownloadedFiles().toMutableList()
        val removed = currentDownloads.removeAll { it.libraryItemId == libraryItemId }
        if (removed) {
            saveDownloadedFiles(currentDownloads)
        }
    }

    private fun saveDownloadedFiles(files: List<DownloadedFile>) {
        val json = gson.toJson(files)
        sharedPreferences.edit().putString(PREF_KEY_DOWNLOADED_FILES, json).apply()
    }
}
