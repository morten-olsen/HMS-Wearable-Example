package com.fprieto.hms.wearable.player

import android.os.Parcelable
import com.google.android.exoplayer2.C
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioPlayerState( // Renamed class
    internal var playWhenReady: Boolean = true, // Default to autoplay for audio
    internal var currentWindow: Int = C.INDEX_UNSET,
    internal var playBackPosition: Long = 0,
    internal var mediaUrl: String? = null, // Renamed from videoUrl
    internal var title: String? = null, // Added for displaying current track/book title
    internal var artist: String? = null // Added for displaying artist/author
) : Parcelable {

    fun checkAndSet(url: String, newTitle: String? = null, newArtist: String? = null): AudioPlayerState =
        if (mediaUrl == url) {
            // Potentially update title/artist even if URL is the same, if they changed
            this.apply{
                title = newTitle ?: title
                artist = newArtist ?: artist
            }
        } else {
            this.apply {
                mediaUrl = url
                title = newTitle
                artist = newArtist
                playBackPosition = 0
                currentWindow = C.INDEX_UNSET
                playWhenReady = true // Reset to autoplay for new media
            }
        }
}