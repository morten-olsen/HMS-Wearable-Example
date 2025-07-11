package com.fprieto.hms.wearable.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fprieto.hms.wearable.data.repository.AudiobookshelfRepository
import com.fprieto.hms.wearable.model.audiobookshelf.Library
import com.fprieto.hms.wearable.net.AuthManager
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class LibraryListResult {
    object Loading : LibraryListResult()
    data class Success(val libraries: List<Library>) : LibraryListResult()
    data class Error(val message: String) : LibraryListResult()
    object Empty : LibraryListResult()
}

class LibraryListViewModel @Inject constructor(
    private val audiobookshelfRepository: AudiobookshelfRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _libraryListResult = MutableLiveData<Event<LibraryListResult>>()
    val libraryListResult: LiveData<Event<LibraryListResult>> = _libraryListResult

    fun fetchLibraries() {
        val token = authManager.getAuthToken()
        if (token.isNullOrEmpty()) {
            _libraryListResult.value = Event(LibraryListResult.Error("User not logged in"))
            // Potentially navigate to login or show a more permanent error
            return
        }

        _libraryListResult.value = Event(LibraryListResult.Loading)
        viewModelScope.launch {
            val result = audiobookshelfRepository.getLibraries(token)
            result.fold(
                onSuccess = { response ->
                    if (response.libraries.isEmpty()) {
                        _libraryListResult.postValue(Event(LibraryListResult.Empty))
                    } else {
                        _libraryListResult.postValue(Event(LibraryListResult.Success(response.libraries)))
                    }
                    Timber.i("Fetched ${response.libraries.size} libraries")
                },
                onFailure = { exception ->
                    Timber.e(exception, "Failed to fetch libraries")
                    _libraryListResult.postValue(Event(LibraryListResult.Error(exception.message ?: "Unknown error fetching libraries")))
                }
            )
        }
    }
}
