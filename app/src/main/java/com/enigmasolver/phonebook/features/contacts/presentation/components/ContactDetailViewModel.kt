package com.enigmasolver.phonebook.features.contacts.presentation.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enigmasolver.phonebook.features.contacts.data.RemoteContactRepository
import com.enigmasolver.phonebook.features.contacts.domain.ContactResponse
import com.enigmasolver.phonebook.shared.AsyncState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailViewModel @Inject constructor(
    private val repository: RemoteContactRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AsyncState<ContactResponse>>(AsyncState.Loading)
    val uiState = _uiState.asStateFlow()

    fun fetchContact(contactId: String) {
        viewModelScope.launch {
            _uiState.value = AsyncState.Loading

            repository.getContactById(contactId)
                .onSuccess { users ->
                    _uiState.value = AsyncState.Success(users)
                }
                .onFailure { error ->
                    _uiState.value = AsyncState.Error(error.message ?: "Unknown Error")
                }
        }
    }
}
