package com.fprieto.hms.wearable.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.fprieto.hms.wearable.di.NetworkModule // For OkHttpClient, though direct injection into Service is tricky without Hilt/manual setup
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class DownloadService : Service() {

    // TODO: Proper DI for OkHttpClient. For now, creating a new instance or accessing via Application class if Dagger is setup there.
    // Since NetworkModule provides OkHttpClient via Dagger, and App is an AndroidInjector,
    // we might need to rethink how service gets this or pass client configuration.
    // For simplicity now, a new client is created. This is NOT ideal for a real app.
    private val client = OkHttpClient()
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    companion object {
        const val ACTION_START_DOWNLOAD = "com.fprieto.hms.wearable.service.action.START_DOWNLOAD"
        const val EXTRA_DOWNLOAD_URL = "com.fprieto.hms.wearable.service.extra.DOWNLOAD_URL"
        const val EXTRA_FILE_NAME = "com.fprieto.hms.wearable.service.extra.FILE_NAME"
        const val EXTRA_LIBRARY_ITEM_ID = "com.fprieto.hms.wearable.service.extra.LIBRARY_ITEM_ID"

        fun startDownload(context: Context, downloadUrl: String, libraryItemId: String, fileName: String) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_START_DOWNLOAD
                putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                putExtra(EXTRA_LIBRARY_ITEM_ID, libraryItemId)
                putExtra(EXTRA_FILE_NAME, fileName)
            }
            context.startService(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_START_DOWNLOAD) {
            val downloadUrl = intent.getStringExtra(EXTRA_DOWNLOAD_URL)
            val libraryItemId = intent.getStringExtra(EXTRA_LIBRARY_ITEM_ID)
            val fileName = intent.getStringExtra(EXTRA_FILE_NAME)

            if (downloadUrl.isNullOrEmpty() || libraryItemId.isNullOrEmpty() || fileName.isNullOrEmpty()) {
                Timber.e("Download URL, Library Item ID, or File Name is missing.")
                stopSelf(startId)
                return START_NOT_STICKY
            }
            Timber.d("Starting download for URL: $downloadUrl, Item ID: $libraryItemId, File: $fileName")
            serviceScope.launch {
                downloadFile(downloadUrl, libraryItemId, fileName, startId)
            }
        }
        return START_REDELIVER_INTENT // Or START_NOT_STICKY depending on desired behavior
    }

    private suspend fun downloadFile(downloadUrl: String, libraryItemId: String, fileName: String, startId: Int) {
        // Define storage directory: app-specific files directory + libraryItemId subdirectory
        val libraryDir = File(getExternalFilesDir(null), libraryItemId)
        if (!libraryDir.exists()) {
            libraryDir.mkdirs()
        }
        val destinationFile = File(libraryDir, fileName)

        if (destinationFile.exists()) {
            Timber.i("File already exists: ${destinationFile.absolutePath}")
            // TODO: Notify about completion or check file integrity
            stopSelf(startId)
            return
        }

        val request = Request.Builder().url(downloadUrl).build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Timber.e("Download failed: ${response.code} ${response.message}")
                // TODO: Notify about failure
                stopSelf(startId)
                return
            }

            response.body?.let { body ->
                val sink = FileOutputStream(destinationFile)
                sink.use { fileOut ->
                    body.byteStream().use { input ->
                        input.copyTo(fileOut)
                    }
                }
                Timber.i("Download successful: ${destinationFile.absolutePath}")
                // TODO: Notify about successful download (e.g., using a LocalBroadcastManager or updating a LiveData)
            } ?: run {
                Timber.e("Download failed: Response body is null.")
            }
        } catch (e: IOException) {
            Timber.e(e, "Download failed due to IOException")
            // TODO: Notify about failure
        } finally {
            stopSelf(startId)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Not a bound service
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        Timber.d("DownloadService destroyed")
    }
}
