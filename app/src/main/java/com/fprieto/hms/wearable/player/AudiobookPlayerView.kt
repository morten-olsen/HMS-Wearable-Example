package com.fprieto.hms.wearable.player

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.fprieto.hms.wearable.databinding.AudioPlayerBinding // To be renamed/created

class AudiobookPlayerView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    private var binding: AudioPlayerBinding // To be renamed/created
    private lateinit var audioComponent: AudiobookPlayerComponent

    init {
        isSaveEnabled = true
        val inflater: LayoutInflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        binding = AudioPlayerBinding.inflate(inflater, this, true) // To be renamed/created
    }

    fun prepareToPlay(owner: LifecycleOwner, playerState: AudioPlayerState) { // Renamed VideoPlayerState
        initAudioComponent(playerState)
        setPlayerState(playerState, owner)
    }

    fun play() {
        audioComponent.setPlayerState(true)
    }

    fun pause() {
        audioComponent.setPlayerState(false)
    }

    fun rewind() {
        binding.playerView.player?.seekTo(binding.playerView.player.currentPosition - 10000L)
    }

    fun fastForward() {
        binding.playerView.player?.seekTo(binding.playerView.player.currentPosition + 10000L)
    }

    fun previous() {
        binding.playerView.player?.seekTo(0)
    }

    fun next() {
        // For audiobooks, 'next' might mean next chapter or next track in a playlist
        // For now, let's assume it goes to the end, or this logic will be refined later.
        binding.playerView.player?.seekTo(binding.playerView.player.contentDuration)
    }

    private fun setPlayerState(playerState: AudioPlayerState, owner: LifecycleOwner) { // Renamed VideoPlayerState
        if (playerState.mediaUrl.isNullOrEmpty()) { // Renamed videoUrl to mediaUrl
            binding.playerView.onPause()
            audioComponent.disposePlayer()
            owner.lifecycle.removeObserver(audioComponent)
        } else {
            owner.lifecycle.removeObserver(audioComponent)
            owner.lifecycle.addObserver(audioComponent)
        }
    }

    private fun initAudioComponent(playerState: AudioPlayerState) { // Renamed VideoPlayerState
        if (::audioComponent.isInitialized.not()) {
            audioComponent = AudiobookPlayerComponent(context, binding.playerView, playerState)
        }
    }
}