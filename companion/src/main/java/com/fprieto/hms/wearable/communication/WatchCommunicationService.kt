package com.fprieto.hms.wearable.communication

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.fprieto.hms.wearable.data.repository.AudiobookshelfRepository
import com.fprieto.hms.wearable.model.audiobookshelf.LibraryItem // Assuming this is the detailed model
import com.fprieto.hms.wearable.model.audiobookshelf.Media // For episode details
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
// import com.huawei.wearengine.HiWear
// import com.huawei.wearengine.device.DeviceClient
// import com.huawei.wearengine.p2p.Message
// import com.huawei.wearengine.p2p.P2pClient
// import com.huawei.wearengine.p2p.Receiver
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
// import javax.inject.Inject // For Dagger

// @Inject lateinit var audiobookshelfRepository: AudiobookshelfRepository // For Dagger
// @Inject lateinit var gson: Gson // For Dagger

class WatchCommunicationService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    // Conceptual Wear Engine Clients - replace with actual SDK initialization
    // private lateinit var p2pClient: P2pClient
    // private lateinit var deviceClient: DeviceClient
    private var connectedWatchDeviceId: String? = null

    // Replace with actual Dagger injection or service locator pattern
    private val gson = Gson()
    private val audiobookshelfRepository: AudiobookshelfRepository by lazy {
        // This is a placeholder for proper injection.
        // In a real app, this would be provided by Dagger or another DI framework.
        // For now, to make it somewhat runnable conceptually:
        // object : AudiobookshelfRepository { /* mock implementations */ }
        throw NotImplementedError("AudiobookshelfRepository not initialized via DI")
    }
    // private val downloadManager: DownloadManager by lazy { DownloadManager(applicationContext) } // Conceptual

    companion object {
        private const val CHUNK_SIZE = 1024 * 50 // 50KB chunks for audio transfer
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("WatchCommunicationService Created")
        // p2pClient = HiWear.getP2pClient(this)
        // deviceClient = HiWear.getDeviceClient(this)
        // findConnectedDevice()
        // registerMessageReceiver()
    }

    private fun findConnectedDevice() {
        /*
        deviceClient.bondedDevices
            .addOnSuccessListener { devices ->
                devices?.find { it.isConnected }?.let {
                    connectedWatchDeviceId = it.uuid
                    Timber.d("Connected watch found: ${it.name}, ID: ${it.uuid}")
                    // Potentially notify UI or other components
                } ?: Timber.w("No connected watch found.")
            }
            .addOnFailureListener { Timber.e(it, "Failed to get bonded devices.") }
        */
    }

    private fun registerMessageReceiver() {
        /*
        val receiver = Receiver { message ->
            Timber.d("Message received from watch")
            message?.data?.let { data ->
                try {
                    // It's safer to deserialize to a base class or try-catch specific types
                    val messageString = String(data, Charsets.UTF_8)
                    Timber.d("Received raw message string: $messageString")

                    // Determine message type before full deserialization if possible, or use a base class with 'type' field
                    // For simplicity, attempting to deserialize to known types with error handling.
                    // A more robust solution would involve a 'type' field in a base JSON object.
                    val wearableMessage = gson.fromJson(messageString, WearableMessage::class.java) // This will get the 'type' field.

                    when (wearableMessage.type) {
                        MessageType.REQUEST_LIBRARY_LIST -> handleRequestLibraryList(gson.fromJson(messageString, RequestLibraryList::class.java))
                        MessageType.REQUEST_ITEM_SYNC -> handleRequestItemSync(gson.fromJson(messageString, RequestItemSync::class.java))
                        MessageType.REPORT_LISTEN_PROGRESS -> handleReportListenProgress(gson.fromJson(messageString, ReportListenProgress::class.java))
                        else -> Timber.w("Unhandled or base message type received: ${wearableMessage.type}")
                    }
                } catch (e: JsonSyntaxException) {
                    Timber.e(e, "JSON Deserialization error for watch message.")
                } catch (e: Exception) {
                    Timber.e(e, "Error processing message from watch.")
                }
            }
        }
        p2pClient.registerReceiver(receiver)
            .addOnSuccessListener { Timber.i("P2P receiver registered successfully.") }
            .addOnFailureListener { Timber.e(it, "Failed to register P2P receiver.") }
        */
    }

    private fun handleRequestLibraryList(request: RequestLibraryList) {
        Timber.d("Handling RequestLibraryList: filter=${request.filter}")
        serviceScope.launch {
            try {
                // val items = audiobookshelfRepository.getLibraryItems(null) // Assuming a method to get all items
                // For now, using a placeholder:
                val placeholderItems = listOf(
                    LibraryItem("id1", Media(mapOf("title" to "Book 1", "authorName" to "Author A")), "book", ServerSettings("",""), User("","","","",0,false,"","",""),0.0,0.0,0L,0L,0L,0L,0L, emptyList(),"",false, emptyList()),
                    LibraryItem("id2", Media(mapOf("title" to "Podcast Series B", "authorName" to "Podcaster B")), "podcast", ServerSettings("",""), User("","","","",0,false,"","",""),0.0,0.0,0L,0L,0L,0L,0L, emptyList(),"",false, emptyList())
                )
                val syncedItems = placeholderItems.mapNotNull { it.toSyncedLibraryItem() }
                sendMessageToWatch(SyncLibraryListResponse(syncedItems))
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch library items for watch.")
                sendMessageToWatch(ErrorMessage(MessageType.REQUEST_LIBRARY_LIST, "Failed to fetch library: ${e.message}"))
            }
        }
    }

    private fun handleRequestItemSync(request: RequestItemSync) {
        Timber.d("Handling RequestItemSync for itemId: ${request.itemId}, type: ${request.mediaType}")
        serviceScope.launch {
            try {
                // val detailedItem = audiobookshelfRepository.getLibraryItemDetails(request.itemId) // Fetch full details
                // Placeholder for actual item fetching:
                val placeholderItem = LibraryItem(request.itemId, Media(mapOf("title" to "Syncing Item: ${request.itemId}", "authorName" to "Some Author")), request.mediaType, ServerSettings("",""), User("","","","",0,false,"","",""),0.0,0.0,0L,0L,0L,0L,0L, emptyList(),"",false, emptyList())

                placeholderItem?.let { item ->
                    val syncedItem = item.toSyncedLibraryItem()
                    val episodes = if (item.mediaType == "podcast") { // Assuming 'podcastSeries' or similar type
                        // item.media.episodes?.mapNotNull { it.toSyncedEpisodeItem() } ?: emptyList()
                        emptyList() // Placeholder
                    } else null

                    sendMessageToWatch(SyncItemMetadata(syncedItem, episodes))

                    // Simulate download
                    val dummyFileContent = "This is a dummy audio file for ${item.media.metadata?.get("title") ?: item.id}."
                    val dummyFile = File(cacheDir, "${item.id}.mp3")
                    dummyFile.writeText(dummyFileContent)
                    Timber.d("Simulated download for ${item.id} to ${dummyFile.absolutePath}")

                    // Start sending file chunks
                    sendFileToWatch(dummyFile, item.id, null) // episodeId is null for book for now

                } ?: sendMessageToWatch(ErrorMessage(MessageType.REQUEST_ITEM_SYNC, "Item ${request.itemId} not found."))
            } catch (e: Exception) {
                Timber.e(e, "Failed to process item sync request for ${request.itemId}.")
                sendMessageToWatch(ErrorMessage(MessageType.REQUEST_ITEM_SYNC, "Sync failed for ${request.itemId}: ${e.message}"))
            }
        }
    }

    private suspend fun sendFileToWatch(file: File, itemId: String, episodeId: String?) {
        withContext(Dispatchers.IO) {
            if (!file.exists()) {
                Timber.e("File does not exist: ${file.path}")
                sendMessageToWatch(SyncAudioComplete(itemId, episodeId, false, errorMessage = "File not found on companion."))
                return@withContext
            }
            FileInputStream(file).use { fis ->
                val fileSize = file.length()
                val totalChunks = (fileSize / CHUNK_SIZE) + if (fileSize % CHUNK_SIZE == 0L) 0 else 1
                var chunkIndex = 0
                val buffer = ByteArray(CHUNK_SIZE)
                var bytesRead: Int

                Timber.d("Starting to send file ${file.name} in $totalChunks chunks.")
                var offset = 0L
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    val actualData = buffer.copyOf(bytesRead)
                    val chunk = SyncAudioChunk(itemId, episodeId, chunkIndex, totalChunks.toInt(), actualData, offset)
                    sendMessageToWatch(chunk)
                    chunkIndex++
                    offset += bytesRead
                    Timber.d("Sent chunk $chunkIndex/$totalChunks for $itemId")
                    delay(100) // Simulate network latency, remove in production
                }
                sendMessageToWatch(SyncAudioComplete(itemId, episodeId, true, filePathOnWatch = "some/path/on/watch/${file.name}"))
                Timber.d("Finished sending file $itemId")
            }
        }
    }


    private fun handleReportListenProgress(progress: ReportListenProgress) {
        Timber.d("Handling ReportListenProgress for itemId: ${progress.itemId}, time: ${progress.currentTime}")
        serviceScope.launch {
            try {
                // audiobookshelfRepository.updateMediaProgress(progress) // Adapt this method or create new one
                Timber.i("Progress for ${progress.itemId} reported to server (simulated).")
                // Optionally, send an ack or updated progress back to watch if logic requires
                // sendMessageToWatch(SyncListenProgressUpdate(...))
            } catch (e: Exception) {
                Timber.e(e, "Failed to report listen progress for ${progress.itemId} to server.")
                // Notify watch of failure?
            }
        }
    }

    private fun sendMessageToWatch(wearableMessage: WearableMessage) {
        /*
        connectedWatchDeviceId?.let { deviceId ->
            val messageJson = gson.toJson(wearableMessage)
            val messageData = messageJson.toByteArray(Charsets.UTF_8)
            val p2pMessage = Message.Builder()
                .setPayload(messageData)
                // .setType(Message.MESSAGE_TYPE_DATA) // Or specific type if Wear Engine uses it
                .build()

            p2pClient.send(deviceId, p2pMessage)
                .addOnSuccessListener { Timber.d("Message of type ${wearableMessage.type} sent to $deviceId successfully. JSON: $messageJson") }
                .addOnFailureListener { Timber.e(it, "Failed to send message to $deviceId. JSON: $messageJson") }
        } ?: Timber.e("No connected watch device ID to send message to. Message was: ${gson.toJson(wearableMessage)}")
        */
       Timber.d("Attempting to send to watch (simulated): ${gson.toJson(wearableMessage)}")
    }

    // Helper extension functions to map domain models to communication models
    fun LibraryItem.toSyncedLibraryItem(): SyncedLibraryItem {
        // Actual mapping logic from your detailed LibraryItem to the simpler SyncedLibraryItem
        return SyncedLibraryItem(
            id = this.id,
            title = this.media.metadata?.get("title") as? String ?: "Unknown Title",
            author = this.media.metadata?.get("authorName") as? String,
            coverUrl = this.media.coverPath, // Assuming this is a URL or can be converted to one
            mediaType = this.mediaType ?: "unknown"
        )
    }

    // fun Media.Episode.toSyncedEpisodeItem(): SyncedEpisodeItem { // Assuming Episode structure
    //     return SyncedEpisodeItem(id = this.id, title = this.title, duration = this.duration, isDownloaded = false)
    // }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("WatchCommunicationService Started")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Not a bound service for now
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("WatchCommunicationService Destroyed")
        serviceJob.cancel()
        // p2pClient.unregisterReceiver(receiver) // Make sure receiver instance is accessible
    }
}
