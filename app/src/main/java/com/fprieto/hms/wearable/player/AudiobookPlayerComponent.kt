package com.fprieto.hms.wearable.player

import kotlin.math.max
import android.content.Context
import android.net.Uri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.Player // For Player.EventListener

internal class AudiobookPlayerComponent( // Renamed class
    private val context: Context,
    private val playerView: PlayerView,
    private val playerState: AudioPlayerState // Renamed state
) : DefaultLifecycleObserver, Player.EventListener { // Added Player.EventListener

    private var player: SimpleExoPlayer? = null

    private fun initPlayer() {
        if (player == null) {
            player = SimpleExoPlayer.Builder(context) // Updated to new ExoPlayer builder
                .setTrackSelector(DefaultTrackSelector(context)) // Simpler track selector for audio
                .build()
            player?.setHandleAudioBecomingNoisy(true) // Important for audio apps
            player?.addListener(this) // Register as listener
        }
        playerView.player = player
        setPlayerParams(playerState)
    }

    private fun setPlayerParams(state: AudioPlayerState) { // Renamed state
        state.mediaUrl?.let { url -> // Renamed to mediaUrl
            val uri: Uri = Uri.parse(url)
            val mediaSource: MediaSource = buildMediaSource(uri)
            player?.playWhenReady = state.playWhenReady
            val hasResumePosition: Boolean =
                state.currentWindow != C.INDEX_UNSET && state.playBackPosition.toInt() != 0
            if (hasResumePosition) {
                player?.seekTo(state.currentWindow, state.playBackPosition)
            }
            val resetState: Boolean = !hasResumePosition
            player?.setMediaSource(mediaSource, resetState) // Updated to setMediaSource
            player?.prepare() // Prepare without reset for setMediaSource
        }
    }

    fun setPlayerState(playWhenReady: Boolean) {
        player?.playWhenReady = playWhenReady
    }

    private fun releasePlayer() {
        updatePlayerStateFromExoPlayer() // Renamed
        disposePlayer()
    }

    internal fun disposePlayer() {
        player?.removeListener(this)
        player?.release()
        player = null
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, "AudiobookshelfWear") // Updated app name
        )
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

    private fun updatePlayerStateFromExoPlayer() { // Renamed and reflects current state from player
        player?.let {
            playerState.playWhenReady = it.playWhenReady
            playerState.currentWindow = it.currentWindowIndex
            playerState.playBackPosition = max(0, it.contentPosition)
        }
    }

    // Player.EventListener methods
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        // Can be used to update UI based on player state (buffering, ended, etc.)
        // For example, show/hide a progress bar
        updatePlayerStateFromExoPlayer()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        // Update UI based on isPlaying state (e.g., change play/pause button icon)
        playerState.playWhenReady = isPlaying // Keep our state in sync
    }

    override fun onPositionDiscontinuity(reason: Int) {
        // Called when playback position changes discontinuously (e.g. seek, media item transition)
        updatePlayerStateFromExoPlayer()
    }

    // Other Player.EventListener methods can be overridden if needed:
    // onTimelineChanged, onTracksChanged, onLoadingChanged, onRepeatModeChanged,
    // onShuffleModeEnabledChanged, onPlayerError, onSeekProcessed, etc.

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (Util.SDK_INT > 23) {
            initPlayer()
            playerView.onResume()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (Util.SDK_INT <= 23) {
            initPlayer()
            playerView.onResume()
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        if (Util.SDK_INT <= 23) {
            playerView.onPause()
            releasePlayer()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        if (Util.SDK_INT > 23) {
            playerView.onPause()
            releasePlayer()
        }
    }
}