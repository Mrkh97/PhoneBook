package com.enigmasolver.phonebook.shared

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<AppEvent>()
    val events = _events.asSharedFlow()

    suspend fun emit(event: AppEvent) = _events.emit(event)
}

sealed interface AppEvent {
    data object RefreshContacts : AppEvent
    data class RefreshContact(val contactId: String) : AppEvent
    data class ShowSuccessSnackbar(val message: String) : AppEvent
}