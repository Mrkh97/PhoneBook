package com.enigmasolver.phonebook.features.contacts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enigmasolver.phonebook.features.contacts.data.LocalContactRepository
import com.enigmasolver.phonebook.features.contacts.data.RemoteContactRepository
import com.enigmasolver.phonebook.features.contacts.domain.ContactResponse
import com.enigmasolver.phonebook.features.contacts.domain.ContactSearchEntity
import com.enigmasolver.phonebook.shared.AppEvent
import com.enigmasolver.phonebook.shared.AppEventBus
import com.enigmasolver.phonebook.shared.AsyncState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val repository: RemoteContactRepository,
    private val localContactRepository: LocalContactRepository,
    private val eventBus: AppEventBus
) : ViewModel() {

    private val _uiState = MutableStateFlow<AsyncState<List<ContactResponse>>>(AsyncState.Loading)
    val uiState = _uiState.asStateFlow()
    private val _searchTerm = MutableStateFlow<List<ContactSearchEntity>>(emptyList())
    val searchTerms = _searchTerm.asStateFlow()

    init {
        fetchContacts()
        fetchSearchTerms()
        viewModelScope.launch {
            eventBus.events.collect {
                if (it is AppEvent.RefreshContacts) fetchContacts()
            }
        }
    }

    fun fetchContacts() {
        viewModelScope.launch {
            _uiState.value = AsyncState.Loading

            repository.listContacts()
                .onSuccess { contacts ->
                    _uiState.value = AsyncState.Success(contacts)
                }
                .onFailure { error ->
                    _uiState.value = AsyncState.Error(error.message ?: "Unknown Error")
                }
        }
    }

    fun fetchSearchTerms() {
        viewModelScope.launch {
            _searchTerm.value = localContactRepository.listSearchTerms()
        }
    }

    fun saveSearchTerm(searchTerm: String) {
        if (searchTerm.isBlank()) return
        if (searchTerms.value.any { it.searchTerm == searchTerm }) return
        viewModelScope.launch {
            localContactRepository.addSearchTerm(searchTerm)
            fetchSearchTerms()
        }
    }

    fun removeSearchTerm(id: Int) {
        viewModelScope.launch {
            localContactRepository.removeSearchTerm(id)
            fetchSearchTerms()
        }
    }

    fun clearAllSearchTerms() {
        viewModelScope.launch {
            localContactRepository.clearAllSearchTerms()
            fetchSearchTerms()

        }
    }


}