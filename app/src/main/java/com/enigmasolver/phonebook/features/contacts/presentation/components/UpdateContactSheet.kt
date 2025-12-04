package com.enigmasolver.phonebook.features.contacts.presentation.components

import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.enigmasolver.phonebook.R
import com.enigmasolver.phonebook.features.contacts.presentation.ErrorContent
import com.enigmasolver.phonebook.shared.AsyncState
import com.enigmasolver.phonebook.shared.components.OutlinedInput
import com.enigmasolver.phonebook.shared.theme.AppColors
import com.enigmasolver.phonebook.shared.theme.AppTypography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateContactSheet(
    contactId: String,
    viewModel: UpdateContactViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val contactState by viewModel.contactState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectPhotoOpen by remember { mutableStateOf(false) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(contactId) {
        viewModel.fetchContact(contactId)
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            sheetState.hide()
            onDismissRequest()
            viewModel.onEvent(UpdateContactEvent.ResetSuccess)
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = onDismissRequest
    ) {
        when (val cState = contactState) {
            is AsyncState.Error -> ErrorContent(
                message = cState.message,
                onRetry = { viewModel.fetchContact(contactId) }
            )

            AsyncState.Loading -> CircularProgressIndicator()
            is AsyncState.Success -> Column(
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
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                                onDismissRequest()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)
                    ) {
                        Text("Cancel", style = AppTypography.titleLargeMedium)
                    }
                    Text(
                        "Edit Contact",
                        style = AppTypography.headlineSmallBold.copy(
                            color = AppColors.Gray950,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { viewModel.onEvent(UpdateContactEvent.Submit) },
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
                if (tempImageUri != null) {
                    AsyncImage(
                        model = tempImageUri,
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    when (val cState = contactState) {
                        is AsyncState.Error -> Image(
                            painter = painterResource(R.drawable.contact),
                            modifier = Modifier.size(96.dp),
                            contentDescription = "Contact Avatar"
                        )

                        is AsyncState.Loading -> Image(
                            painter = painterResource(R.drawable.contact),
                            modifier = Modifier.size(96.dp),
                            contentDescription = "Contact Avatar"
                        )

                        is AsyncState.Success ->
                            ContactAvatar(
                                imageUrl = cState.data.profileImageUrl,
                                name = cState.data.firstName,
                                modifier = Modifier.size(96.dp),
                            )
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                TextButton({
                    selectPhotoOpen = true
                }) {
                    Text("Change Photo")
                }
                Spacer(modifier = Modifier.size(32.dp))
                OutlinedInput(
                    placeholder = "First Name",
                    initialValue = state.name,
                    onValueChange = { viewModel.onEvent(UpdateContactEvent.NameChanged(it)) },
                )
                Spacer(modifier = Modifier.size(12.dp))
                OutlinedInput(
                    placeholder = "Last Name",
                    initialValue = state.surname,
                    onValueChange = { viewModel.onEvent(UpdateContactEvent.SurnameChanged(it)) },
                )
                Spacer(modifier = Modifier.size(12.dp))
                OutlinedInput(
                    placeholder = "Phone Number",
                    initialValue = state.phoneNumber,
                    onValueChange = { viewModel.onEvent(UpdateContactEvent.PhoneChanged(it)) },
                )
                Spacer(modifier = Modifier.size(12.dp))
                if (state.errorMessage != null) {
                    Text(
                        state.errorMessage ?: "Hata",
                        color = AppColors.RedDelete,
                        style = AppTypography.titleMediumRegular
                    )
                }
            }
        }


    }
    if (selectPhotoOpen)
        SelectPhotoSheet(
            onDismissRequest = { selectPhotoOpen = false },
            onImageUriSet = {
                viewModel.onEvent(UpdateContactEvent.ImageChanged(it))
                tempImageUri = it
                selectPhotoOpen = false
            })
}