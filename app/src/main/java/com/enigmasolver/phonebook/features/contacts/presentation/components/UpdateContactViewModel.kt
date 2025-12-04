package com.enigmasolver.phonebook.features.contacts.presentation.components

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enigmasolver.phonebook.features.contacts.data.RemoteContactRepository
import com.enigmasolver.phonebook.features.contacts.domain.ContactResponse
import com.enigmasolver.phonebook.shared.AppEvent
import com.enigmasolver.phonebook.shared.AppEventBus
import com.enigmasolver.phonebook.shared.AsyncState
import com.enigmasolver.phonebook.shared.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UpdateContactViewModel @Inject constructor(
    private val repository: RemoteContactRepository,
    private val eventBus: AppEventBus,
) : ViewModel() {
    private val _state = MutableStateFlow(UpdateContactState())
    val state = _state.asStateFlow()
    private val _contactState = MutableStateFlow<AsyncState<ContactResponse>>(AsyncState.Loading)
    val contactState = _contactState.asStateFlow()

    fun fetchContact(contactId: String) {
        viewModelScope.launch {
            _contactState.value = AsyncState.Loading

            repository.getContactById(contactId)
                .onSuccess { contact ->
                    _contactState.value = AsyncState.Success(contact)
                    _state.update {
                        it.copy(
                            name = contact.firstName,
                            surname = contact.lastName,
                            phoneNumber = contact.phoneNumber,
                            localUri = null,
                        )
                    }
                }
                .onFailure { error ->
                    _contactState.value = AsyncState.Error(error.message ?: "Unknown Error")
                }
        }
    }

    fun onEvent(event: UpdateContactEvent) {
        when (event) {
            is UpdateContactEvent.NameChanged -> {
                _state.update { it.copy(name = event.value) }
            }

            is UpdateContactEvent.SurnameChanged -> {
                _state.update { it.copy(surname = event.value) }
            }

            is UpdateContactEvent.PhoneChanged -> {
                _state.update { it.copy(phoneNumber = event.value) }
            }

            is UpdateContactEvent.ImageChanged -> {
                _state.update { it.copy(localUri = event.value) }
            }

            is UpdateContactEvent.Submit -> {
                submitContact()
            }

            is UpdateContactEvent.ResetSuccess -> {
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
            val result = repository.updateContact(
                id = (_contactState.value as AsyncState.Success).data.id,
                name = currentState.name,
                surname = currentState.surname,
                phone = currentState.phoneNumber,
                imageUrl = serverImageUrl
                    ?: (_contactState.value as AsyncState.Success).data.profileImageUrl
            )
            if (result.isSuccess) {
                _state.update { UpdateContactState(isSuccess = true) }
                eventBus.emit(AppEvent.RefreshContacts)
                eventBus.emit(AppEvent.ShowSuccessSnackbar("User is updated!"))
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

data class UpdateContactState(
    val name: String = "",
    val surname: String = "",
    val phoneNumber: String = "",
    var localUri: Uri? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed interface UpdateContactEvent {
    data class NameChanged(val value: String) : UpdateContactEvent
    data class SurnameChanged(val value: String) : UpdateContactEvent
    data class PhoneChanged(val value: String) : UpdateContactEvent
    data class ImageChanged(val value: Uri) : UpdateContactEvent
    data object Submit : UpdateContactEvent
    data object ResetSuccess : UpdateContactEvent
}