package com.fprieto.hms.wearable.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fprieto.hms.wearable.data.repository.AudiobookshelfRepository
import com.fprieto.hms.wearable.model.audiobookshelf.LoginResponse
import com.fprieto.hms.wearable.net.AuthManager
import com.fprieto.hms.wearable.net.LoginRequest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class LoginResult {
    object Loading : LoginResult()
    data class Success(val loginResponse: LoginResponse) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class LoginViewModel @Inject constructor(
    private val audiobookshelfRepository: AudiobookshelfRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _loginResult = MutableLiveData<Event<LoginResult>>()
    val loginResult: LiveData<Event<LoginResult>> = _loginResult

    fun login(serverUrl: String, username: String, password: String) {
        _loginResult.value = Event(LoginResult.Loading)
        viewModelScope.launch {
            // Basic URL validation, can be improved
            val urlToUse = if (serverUrl.startsWith("http://") || serverUrl.startsWith("https://")) {
                if (serverUrl.endsWith("/")) serverUrl else "$serverUrl/"
            } else {
                "http://$serverUrl/" // Default to http if no scheme, add trailing slash
            }
            authManager.saveBaseUrl(urlToUse) // Save for Retrofit instance recreation or next app start

            // Ideally, Retrofit client should be reconfigured here if base URL changes dynamically.
            // For now, we rely on the fact that NetworkModule will pick up the new URL
            // when the app is next fully initialized or if Dagger re-provides it.
            // This is a simplification for now.

            val result = audiobookshelfRepository.login(urlToUse, LoginRequest(username, password))
            result.fold(
                onSuccess = { response ->
                    authManager.saveAuthToken(response.user.token)
                    authManager.saveUserDetails(response.user.id, response.user.username)
                    _loginResult.postValue(Event(LoginResult.Success(response)))
                    Timber.i("Login Successful for ${response.user.username}")
                },
                onFailure = { exception ->
                    Timber.e(exception, "Login failed")
                    _loginResult.postValue(Event(LoginResult.Error(exception.message ?: "Unknown login error")))
                }
            )
        }
    }
}
