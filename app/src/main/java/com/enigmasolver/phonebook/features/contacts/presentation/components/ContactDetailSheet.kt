package com.enigmasolver.phonebook.features.contacts.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enigmasolver.phonebook.features.contacts.presentation.ErrorContent
import com.enigmasolver.phonebook.shared.AsyncState
import com.enigmasolver.phonebook.shared.components.OutlinedInput
import com.enigmasolver.phonebook.shared.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailSheet(
    viewModel: ContactDetailViewModel = hiltViewModel(),
    contactId: String,
    onDismissRequest: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var glowColor by remember { mutableStateOf(Color.Transparent) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    LaunchedEffect(contactId) {
        viewModel.fetchContact(contactId)
    }
    ModalBottomSheet(
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = onDismissRequest
    ) {

        when (val currentState = state) {
            is AsyncState.Loading -> {
                CircularProgressIndicator()
            }

            is AsyncState.Error -> {
                ErrorContent(
                    message = currentState.message,
                    onRetry = { viewModel.fetchContact(contactId) }
                )
            }

            is AsyncState.Success -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxHeight(0.85f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentWidth(Alignment.End)
                        ) {
                            Text(
                                "Done",
                                style = AppTypography.titleLargeBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(48.dp))
                    ContactAvatar(
                        imageUrl = currentState.data.profileImageUrl,
                        name = currentState.data.firstName,
                        onMainColorLoaded = { glowColor = it },
                        modifier = Modifier
                            .size(96.dp)
                            .shadow(
                                shape = RoundedCornerShape(999.dp),
                                elevation = 24.dp,
                                ambientColor = glowColor,
                                spotColor = glowColor,

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

                }
            }
        }


    }
}