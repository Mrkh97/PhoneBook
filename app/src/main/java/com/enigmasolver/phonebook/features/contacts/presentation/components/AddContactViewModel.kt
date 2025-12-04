package com.enigmasolver.phonebook.features.contacts.presentation.components

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enigmasolver.phonebook.features.contacts.data.RemoteContactRepository
import com.enigmasolver.phonebook.shared.AppEvent
import com.enigmasolver.phonebook.shared.AppEventBus
import com.enigmasolver.phonebook.shared.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val repository: RemoteContactRepository,
    private val eventBus: AppEventBus,
) : ViewModel() {
    private val _state = MutableStateFlow(AddContactState())
    val state = _state.asStateFlow()

    fun onEvent(event: AddContactEvent) {
        when (event) {
            is AddContactEvent.NameChanged -> {
                _state.update { it.copy(name = event.value) }
            }

            is AddContactEvent.SurnameChanged -> {
                _state.update { it.copy(surname = event.value) }
            }

            is AddContactEvent.PhoneChanged -> {
                _state.update { it.copy(phoneNumber = event.value) }
            }

            is AddContactEvent.ImageChanged -> {
                _state.update { it.copy(localUri = event.value) }
            }

            is AddContactEvent.Submit -> {
                submitContact()
            }

            is AddContactEvent.ResetSuccess -> {
                _state.update { it.copy(isSuccess = false) }
            }
        }
    }

    private fun submitContact() {
        val currentState = _state.value
        if (currentState.name.isBlank() || currentState.phoneNumber.isBlank()) {
            _state.update { it.copy(errorMessage = "Name and Phone Number are required") }
            return
        }
        if (!ValidationUtils.isValidPhone(currentState.phoneNumber)) {
            _state.update { it.copy(errorMessage = "Phone Number Invalid") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            var serverImageUrl: String? = null
            val localUri = currentState.localUri

            if (localUri != null) {
                try {
                    val uploadResponse = repository.uploadImage(localUri)
                    serverImageUrl = uploadResponse.getOrNull()
                    if (serverImageUrl == null) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = (uploadResponse.exceptionOrNull()?.message
                                    ?: "Image Upload Failed")
                            )
                        }
                        return@launch
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Image Error: ${e.message}"
                        )
                    }
                    return@launch
                }
            }
            val result = repository.addContact(
                name = currentState.name,
                surname = currentState.surname,
                phone = currentState.phoneNumber,
                imageUrl = serverImageUrl
            )
            if (result.isSuccess) {
                _state.update { AddContactState(isSuccess = true) }
                eventBus.emit(AppEvent.RefreshContacts)
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }
}

data class AddContactState(
    val name: String = "",
    val surname: String = "",
    val phoneNumber: String = "",
    var localUri: Uri? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed interface AddContactEvent {
    data class NameChanged(val value: String) : AddContactEvent
    data class SurnameChanged(val value: String) : AddContactEvent
    data class PhoneChanged(val value: String) : AddContactEvent
    data class ImageChanged(val value: Uri) : AddContactEvent
    data object Submit : AddContactEvent
    data object ResetSuccess : AddContactEvent
}