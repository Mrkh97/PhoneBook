package com.enigmasolver.phonebook.features.contacts.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enigmasolver.phonebook.shared.theme.AppColors
import com.enigmasolver.phonebook.shared.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteContactSheet(
    viewModel: DeleteContactViewModel = hiltViewModel(),
    contactId: String,
    onContactDeleted: () -> Unit,
    onCancelTapped: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onContactDeleted()
            viewModel.onEvent(DeleteContactEvent.ResetSuccess)
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "Delete Contact",
                style = AppTypography.headlineSmallBold.copy(color = AppColors.Gray900)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                "Are you sure you want to delete this contact?",
                style = AppTypography.titleMediumMedium.copy(color = AppColors.Gray900)
            )
            Spacer(modifier = Modifier.size(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onCancelTapped,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = AppColors.Gray950,
                        disabledContainerColor = Color.White,
                        disabledContentColor = AppColors.Gray500,
                    ),
                    border = BorderStroke(1.dp, AppColors.Gray950),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text("No", style = AppTypography.titleLargeSemiBold)
                }
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = { viewModel.onEvent(DeleteContactEvent.Submit(contactId)) },
                    colors = ButtonColors(
                        containerColor = AppColors.Gray950,
                        contentColor = Color.White,
                        disabledContainerColor = AppColors.Gray500,
                        disabledContentColor = Color.White,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text(
                        "Yes",
                        style = AppTypography.titleLargeSemiBold
                    )
                }
            }
        }

    }
}