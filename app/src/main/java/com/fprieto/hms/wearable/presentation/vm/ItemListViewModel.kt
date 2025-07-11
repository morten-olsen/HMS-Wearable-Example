package com.fprieto.hms.wearable.presentation.vm

import androidx.lifecycle.*
import com.fprieto.hms.wearable.data.repository.AudiobookshelfRepository
import com.fprieto.hms.wearable.model.audiobookshelf.LibraryItem
import com.fprieto.hms.wearable.net.AuthManager
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class ItemListResult {
    object Loading : ItemListResult()
    data class Success(val items: List<LibraryItem>) : ItemListResult()
    data class Error(val message: String) : ItemListResult()
    object Empty : ItemListResult()
}

class ItemListViewModel @Inject constructor(
    private val audiobookshelfRepository: AudiobookshelfRepository,
    private val authManager: AuthManager,
    private val savedStateHandle: SavedStateHandle // For accessing navigation arguments
) : ViewModel() {

    private val _itemListResult = MutableLiveData<Event<ItemListResult>>()
    val itemListResult: LiveData<Event<ItemListResult>> = _itemListResult

    val libraryId: String? = savedStateHandle.get<String>("libraryId")
    val libraryName: String? = savedStateHandle.get<String>("libraryName")


    fun fetchItems(libId: String? = libraryId) {
        val token = authManager.getAuthToken()
        if (token.isNullOrEmpty()) {
            _itemListResult.value = Event(ItemListResult.Error("User not logged in"))
            return
        }
        if (libId.isNullOrEmpty()) {
            _itemListResult.value = Event(ItemListResult.Error("Library ID is missing"))
            return
        }

        _itemListResult.value = Event(ItemListResult.Loading)
        viewModelScope.launch {
            // TODO: Implement pagination later if needed
            val result = audiobookshelfRepository.getLibraryItems(token, libId, limit = 100, page = 0)
            result.fold(
                onSuccess = { response ->
                    if (response.results.isEmpty()) {
                        _itemListResult.postValue(Event(ItemListResult.Empty))
                    } else {
                        _itemListResult.postValue(Event(ItemListResult.Success(response.results)))
                    }
                    Timber.i("Fetched ${response.results.size} items for library $libId")
                },
                onFailure = { exception ->
                    Timber.e(exception, "Failed to fetch items for library $libId")
                    _itemListResult.postValue(Event(ItemListResult.Error(exception.message ?: "Unknown error fetching items")))
                }
            )
        }
    }
}
