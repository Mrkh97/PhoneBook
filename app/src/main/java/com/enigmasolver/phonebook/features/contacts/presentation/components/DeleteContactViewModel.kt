package com.enigmasolver.phonebook.features.contacts.presentation.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enigmasolver.phonebook.features.contacts.data.RemoteContactRepository
import com.enigmasolver.phonebook.shared.AppEvent
import com.enigmasolver.phonebook.shared.AppEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DeleteContactViewModel @Inject constructor(
    private val repository: RemoteContactRepository,
    private val eventBus: AppEventBus
) : ViewModel() {
    private val _state = MutableStateFlow(DeleteContactState())
    val state = _state.asStateFlow()

    fun onEvent(event: DeleteContactEvent) {
        when (event) {

            is DeleteContactEvent.Submit -> {
                submitContact(event.id)
            }

            is DeleteContactEvent.ResetSuccess -> {
                _state.update { it.copy(isSuccess = false) }
            }
        }
    }

    private fun submitContact(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.deleteContactWithId(id)
            if (result.isSuccess) {
                _state.update {
                    DeleteContactState(isSuccess = true)
                }
                eventBus.emit(AppEvent.RefreshContacts)
                eventBus.emit(AppEvent.ShowSuccessSnackbar("User is deleted!"))
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

data class DeleteContactState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

sealed interface DeleteContactEvent {
    data class Submit(val id: String) : DeleteContactEvent
    data object ResetSuccess : DeleteContactEvent
}