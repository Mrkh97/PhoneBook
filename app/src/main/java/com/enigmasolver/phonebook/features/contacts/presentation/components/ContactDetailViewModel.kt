package com.enigmasolver.phonebook.features.contacts.presentation.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enigmasolver.phonebook.features.contacts.data.RemoteContactRepository
import com.enigmasolver.phonebook.features.contacts.domain.ContactResponse
import com.enigmasolver.phonebook.shared.AppEvent
import com.enigmasolver.phonebook.shared.AppEventBus
import com.enigmasolver.phonebook.shared.AsyncState
import com.enigmasolver.phonebook.shared.PhoneBookManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailViewModel @Inject constructor(
    private val repository: RemoteContactRepository,
    private val phoneBookManager: PhoneBookManager,
    private val appEventBus: AppEventBus
) : ViewModel() {
    private val _uiState = MutableStateFlow<AsyncState<ContactResponse>>(AsyncState.Loading)
    val uiState = _uiState.asStateFlow()
    val eventBus = appEventBus

    private val _contactExists = MutableStateFlow(false)
    val contactExists = _contactExists.asStateFlow()

    init {
        viewModelScope.launch {
            eventBus.events.collect {
                if (it is AppEvent.RefreshContact) fetchContact(it.contactId)
            }
        }
    }

    fun fetchContact(contactId: String) {
        viewModelScope.launch {
            _uiState.value = AsyncState.Loading

            repository.getContactById(contactId)
                .onSuccess { contact ->
                    _uiState.value = AsyncState.Success(contact)
                    checkIfContactExists(contact.phoneNumber)
                }
                .onFailure { error ->
                    _uiState.value = AsyncState.Error(error.message ?: "Unknown Error")
                }
        }
    }

    fun checkIfContactExists(phone: String) {
        viewModelScope.launch {
            _contactExists.value = phoneBookManager.contactExists(phone)
        }
    }

    fun saveContact() {
        val currentState = uiState.value as AsyncState.Success<ContactResponse>;
        viewModelScope.launch {
            phoneBookManager.saveContact(
                currentState.data.firstName,
                currentState.data.lastName,
                currentState.data.phoneNumber
            )
            appEventBus.emit(AppEvent.ShowSuccessSnackbar(message = "User is added yo your phone!"))
            _contactExists.value = phoneBookManager.contactExists(currentState.data.phoneNumber)
        }

    }
}
