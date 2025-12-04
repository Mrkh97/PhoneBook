package com.enigmasolver.phonebook.features.contacts.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enigmasolver.phonebook.R
import com.enigmasolver.phonebook.features.contacts.domain.ContactResponse
import com.enigmasolver.phonebook.features.contacts.presentation.components.AddContactSheet
import com.enigmasolver.phonebook.features.contacts.presentation.components.ContactAvatar
import com.enigmasolver.phonebook.features.contacts.presentation.components.ContactDetailSheet
import com.enigmasolver.phonebook.features.contacts.presentation.components.DeleteContactSheet
import com.enigmasolver.phonebook.features.contacts.presentation.components.UpdateContactSheet
import com.enigmasolver.phonebook.shared.AsyncState
import com.enigmasolver.phonebook.shared.components.SearchInput
import com.enigmasolver.phonebook.shared.components.SwipeableItem
import com.enigmasolver.phonebook.shared.extensions.turkishToLowerEnglish
import com.enigmasolver.phonebook.shared.theme.AppColors
import com.enigmasolver.phonebook.shared.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var addContactOpen by remember { mutableStateOf(false) }
    var updateContactOpen by remember { mutableStateOf(false) }
    var contactDetailOpen by remember { mutableStateOf(false) }
    var deleteContactOpen by remember { mutableStateOf(false) }
    var selectedContactId by remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            TopAppBar(
                actions = {
                    Image(
                        painter = painterResource(R.drawable.add),
                        contentDescription = "Add Contact",
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable {
                                addContactOpen = true
                            }
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                },
                title = { Text("Contacts", style = AppTypography.headlineExtraBold) },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = AppColors.Gray950,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),

                )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is AsyncState.Loading -> {
                    CircularProgressIndicator()
                }

                is AsyncState.Error -> {
                    ErrorContent(
                        message = currentState.message,
                        onRetry = { viewModel.fetchContacts() }
                    )
                }

                is AsyncState.Success -> {
                    if (currentState.data.isEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.contact),
                                contentDescription = "Contact Image",
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                "No Contacts",
                                style = AppTypography.headlineBold.copy(color = AppColors.Gray950)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                "Contacts you’ve added will appear here.",
                                style = AppTypography.titleLargeMedium.copy(color = AppColors.Gray900)
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            TextButton(
                                onClick = { addContactOpen = true },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(20.dp)

                            ) {
                                Text("Create New Contact", style = AppTypography.titleLargeBold)
                            }
                        }
                    } else {
                        ContactList(
                            contacts = currentState.data,
                            onDeleteContact = {
                                selectedContactId = it
                                deleteContactOpen = true
                            },
                            onOpenDetail = {
                                selectedContactId = it
                                contactDetailOpen = true
                            },
                            onUpdateContact = {
                                selectedContactId = it
                                updateContactOpen = true
                            }
                        )
                    }
                }
            }


            if (addContactOpen) {
                AddContactSheet(
                    onDismissRequest = {
                        addContactOpen = false
                    }
                )
            }
            if (updateContactOpen) {
                UpdateContactSheet(
                    contactId = selectedContactId,
                    onDismissRequest = {
                        updateContactOpen = false
                    }
                )
            }
            if (deleteContactOpen) {
                DeleteContactSheet(
                    contactId = selectedContactId,
                    onDismissRequest = { deleteContactOpen = false }
                )
            }
            if (contactDetailOpen) {
                ContactDetailSheet(
                    contactId = selectedContactId,
                    onDismissRequest = {
                        contactDetailOpen = false
                    }
                )
            }
        }
    }
}

@Composable
fun ContactList(
    contacts: List<ContactResponse>,
    onDeleteContact: (String) -> Unit,
    onOpenDetail: (String) -> Unit,
    onUpdateContact: (String) -> Unit,
    viewModel: ContactsViewModel = hiltViewModel(),
) {
    var searchHasFocus by remember { mutableStateOf(false) }
    var searchValue by remember { mutableStateOf("") }
    val groupedContacts = remember(contacts, searchValue) {
        contacts
            .filter {
                "${it.firstName} ${it.lastName}".turkishToLowerEnglish()
                    .contains(searchValue.turkishToLowerEnglish())
            }
            .groupBy { it.firstName.first().uppercaseChar() }.toSortedMap()
    }
    val focusManager = LocalFocusManager.current

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                focusManager.clearFocus()
                return Offset.Zero
            }
        }
    }
    val state by viewModel.searchTerms.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .padding(16.dp)
            .nestedScroll(nestedScrollConnection)
    ) {
        SearchInput(
            placeholder = "Search by name",
            initialValue = searchValue,
            onValueChange = { searchValue = it },
            modifier = Modifier
                .onFocusChanged {
                    searchHasFocus = it.hasFocus
                    if (!searchHasFocus && searchValue.isNotBlank()) {
                        viewModel.saveSearchTerm(searchValue)
                    }
                }

        )
        if (searchHasFocus && searchValue.isEmpty())
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "SEARCH HISTORY",
                        style = AppTypography.titleMediumSemiBold.copy(color = AppColors.Gray300)
                    )
                    TextButton(
                        onClick = {
                            viewModel.clearAllSearchTerms()
                        },
                    ) {
                        Text(
                            "Clear All",
                            style = AppTypography.bodyRegular.copy(),
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
                Spacer(modifier = Modifier.size(12.dp))
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                ) {
                    state.forEachIndexed { index, it ->
                        Card(
                            colors = CardColors(
                                containerColor = Color.White,
                                contentColor = AppColors.Gray700,
                                disabledContainerColor = Color.Transparent,
                                disabledContentColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { searchValue = it.searchTerm }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        viewModel.removeSearchTerm(it.id)
                                    },
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Icon(
                                        Icons.Sharp.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(it.searchTerm)
                            }
                            if (index != state.lastIndex)
                                HorizontalDivider(thickness = 1.dp, color = AppColors.Gray50)
                        }
                    }
                }
            }
        else
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),

                ) {
                groupedContacts.forEach { (initial, contactsForThisLetter) ->
                    item {
                        ContactGroup(
                            initial,
                            contactsForThisLetter,
                            onDeleteContact,
                            onOpenDetail,
                            onUpdateContact
                        )
                    }
                }
            }

    }
}

@Composable
fun ContactGroup(
    initial: Char,
    contacts: List<ContactResponse>,
    onDeleteContact: (String) -> Unit,
    onOpenDetail: (String) -> Unit,
    onUpdateContact: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
    ) {
        CharacterHeader(initial)
        HorizontalDivider(
            color = AppColors.Gray50,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        contacts.forEach { contact ->
            SwipeableItem(
                onEdit = {
                    onUpdateContact(contact.id)
                },
                onDelete = {
                    onDeleteContact(contact.id)
                }
            ) {
                ContactCard(contact, modifier = Modifier.clickable {
                    onOpenDetail(contact.id)
                })
            }
            HorizontalDivider(
                color = AppColors.Gray50,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun CharacterHeader(char: Char) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Text(
            text = char.toString(),
            style = AppTypography.titleMediumSemiBold.copy(color = AppColors.Gray300),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }

}

@Composable
fun ContactCard(
    contact: ContactResponse,
    modifier: Modifier = Modifier
) {

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(0),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = AppColors.Gray950,
            disabledContainerColor = AppColors.Gray100,
            disabledContentColor = AppColors.Gray500,
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            ContactAvatar(imageUrl = contact.profileImageUrl, name = contact.firstName)
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                Text(
                    text = "${contact.firstName} ${contact.lastName}",
                    style = AppTypography.titleMediumBold.copy(color = AppColors.Gray900)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = contact.phoneNumber,
                    style = AppTypography.bodyMedium.copy(color = AppColors.Gray500)
                )
            }
        }

    }
}

@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}