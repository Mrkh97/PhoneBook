package com.enigmasolver.phonebook.features.contacts.presentation.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.MoreVert
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enigmasolver.phonebook.R
import com.enigmasolver.phonebook.features.contacts.presentation.ErrorContent
import com.enigmasolver.phonebook.shared.AppEvent
import com.enigmasolver.phonebook.shared.AsyncState
import com.enigmasolver.phonebook.shared.components.OutlinedInput
import com.enigmasolver.phonebook.shared.components.SuccessToast
import com.enigmasolver.phonebook.shared.theme.AppColors
import com.enigmasolver.phonebook.shared.theme.AppTypography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailSheet(
    viewModel: ContactDetailViewModel = hiltViewModel(),
    contactId: String,
    onDismissRequest: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val contactExists by viewModel.contactExists.collectAsStateWithLifecycle()
    var glowColor by remember { mutableStateOf(Color.Transparent) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )
    val sheetSnackbarState = remember { SnackbarHostState() }
    LaunchedEffect(contactId) {
        viewModel.fetchContact(contactId)
        permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
        permissionLauncher.launch(android.Manifest.permission.WRITE_CONTACTS)
        viewModel.eventBus.events.collect { event ->
            if (event is AppEvent.ShowSuccessSnackbar) {
                sheetSnackbarState.showSnackbar(event.message)
            }
        }
    }
    var expanded by remember { mutableStateOf(false) }
    var deleteContactOpen by remember { mutableStateOf(false) }
    var updateContactOpen by remember { mutableStateOf(false) }

    ModalBottomSheet(
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = onDismissRequest
    ) {

        when (val currentState = state) {
            is AsyncState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AsyncState.Error -> {
                ErrorContent(
                    message = currentState.message,
                    onRetry = { viewModel.fetchContact(contactId) }
                )
            }

            is AsyncState.Success -> {
                Scaffold(
                    containerColor = Color.Transparent,
                    snackbarHost = {
                        SnackbarHost(hostState = sheetSnackbarState) { data ->
                            SuccessToast(message = data.visuals.message)
                        }
                    },
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxHeight(0.85f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box() {
                                IconButton(
                                    onClick = {
                                        expanded = true
                                    },
                                    modifier = Modifier
                                        .wrapContentWidth(Alignment.End)
                                ) {
                                    Icon(
                                        imageVector = Icons.Sharp.MoreVert,
                                        contentDescription = "More Icon"
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = {
                                        expanded = false
                                    },
                                    containerColor = Color.White,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .width(165.dp),


                                    ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Edit",
                                                style = AppTypography.titleMediumMedium.copy(color = AppColors.Gray900)
                                            )
                                        },
                                        onClick = {
                                            updateContactOpen = true
                                            expanded = false
                                        },
                                        trailingIcon = {
                                            Image(
                                                painter = painterResource(R.drawable.edit),
                                                contentDescription = null,
                                                colorFilter = ColorFilter.tint(AppColors.Gray950)
                                            )
                                        },

                                        )
                                    HorizontalDivider(color = AppColors.Gray50, thickness = 1.dp)
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Delete",
                                                style = AppTypography.titleMediumMedium.copy(color = AppColors.RedDelete)
                                            )
                                        },
                                        onClick = {
                                            deleteContactOpen = true
                                            expanded = false
                                        },
                                        trailingIcon = {
                                            Image(
                                                painter = painterResource(R.drawable.trash),
                                                contentDescription = null,
                                                colorFilter = ColorFilter.tint(AppColors.RedDelete)
                                            )
                                        },

                                        )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(48.dp))
                        ContactAvatar(
                            imageUrl = currentState.data.profileImageUrl,
                            name = currentState.data.firstName,
                            onMainColorLoaded = { glowColor = it },
                            modifier = Modifier
                                .size(96.dp)
                                .dropShadow(
                                    shape = RoundedCornerShape(999.dp),
                                    shadow = androidx.compose.ui.graphics.shadow.Shadow(
                                        color = glowColor,
                                        radius = 48.dp,
                                        spread = 0.dp
                                    ),
                                )
                        )
                        Spacer(modifier = Modifier.size(32.dp))
                        OutlinedInput(
                            placeholder = "First Name",
                            initialValue = currentState.data.firstName,
                            onValueChange = { },
                            readOnly = true
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        OutlinedInput(
                            placeholder = "Last Name",
                            initialValue = currentState.data.lastName,
                            onValueChange = { },
                            readOnly = true
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        OutlinedInput(
                            placeholder = "Phone Number",
                            initialValue = currentState.data.phoneNumber,
                            onValueChange = { },
                            readOnly = true
                        )
                        Spacer(modifier = Modifier.size(49.dp))
                        OutlinedButton(
                            enabled = !contactExists,
                            onClick = {
                                viewModel.saveContact()
                            },
                            colors = ButtonColors(
                                containerColor = Color.White,
                                contentColor = AppColors.Gray950,
                                disabledContainerColor = Color.White,
                                disabledContentColor = AppColors.Gray200,
                            ),
                            border = BorderStroke(
                                1.dp,
                                color = if (contactExists) AppColors.Gray100 else AppColors.Gray950
                            ),
                            modifier = Modifier
                                .height(56.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    8.dp,
                                    Alignment.CenterHorizontally
                                )
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.save),
                                    contentDescription = "Save Icon"
                                )
                                Text(
                                    "Save to My Phone Contact",
                                    style = AppTypography.titleLargeSemiBold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        if (contactExists)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    8.dp,
                                    Alignment.CenterHorizontally
                                )
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.info),
                                    contentDescription = "Save Icon",
                                    colorFilter = ColorFilter.tint(AppColors.Gray500)
                                )
                                Text(
                                    "This contact is already saved your phone.",
                                    style = AppTypography.bodyMedium.copy(color = AppColors.Gray500)
                                )
                            }
                    }
                }
            }
        }
        if (deleteContactOpen) {
            DeleteContactSheet(
                contactId = contactId,
                onDismissRequest = {
                    deleteContactOpen = false
                    scope.launch {
                        sheetState.hide()
                        onDismissRequest()
                    }
                }
            )
        }

        if (updateContactOpen) {
            UpdateContactSheet(
                contactId = contactId,
                onDismissRequest = {
                    updateContactOpen = false
                    scope.launch {
                        sheetState.hide()
                        onDismissRequest()
                    }
                }
            )
        }

    }
}