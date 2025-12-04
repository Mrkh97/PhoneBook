package com.enigmasolver.phonebook.features.contacts.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.enigmasolver.phonebook.R
import com.enigmasolver.phonebook.shared.extensions.createImageFile
import com.enigmasolver.phonebook.shared.theme.AppColors
import com.enigmasolver.phonebook.shared.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPhotoSheet(
    onImageUriSet: (Uri) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onImageUriSet(uri)
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            val uri = tempCameraUri
            if (success && uri != null) {
                onImageUriSet(uri)
            }
        }
    )


    ModalBottomSheet(
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val uri = context.createImageFile()
                    tempCameraUri = uri
                    cameraLauncher.launch(uri)
                },
                colors = ButtonColors(
                    containerColor = Color.White,
                    contentColor = AppColors.Gray950,
                    disabledContainerColor = Color.White,
                    disabledContentColor = AppColors.Gray500,
                ),
                border = BorderStroke(1.dp, AppColors.Gray950),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                ) {
                    Image(
                        painter = painterResource(R.drawable.camera),
                        contentDescription = "Camera Icon"
                    )
                    Text("Camera", style = AppTypography.titleLargeSemiBold)
                }
            }
            OutlinedButton(
                onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                colors = ButtonColors(
                    containerColor = Color.White,
                    contentColor = AppColors.Gray950,
                    disabledContainerColor = Color.White,
                    disabledContentColor = AppColors.Gray500,
                ),
                border = BorderStroke(1.dp, AppColors.Gray950),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    Image(
                        painter = painterResource(R.drawable.gallery),
                        contentDescription = "Gallery Icon"
                    )
                    Text("Gallery", style = AppTypography.titleLargeSemiBold)
                }
            }
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(
                    "Cancel",
                    style = AppTypography.titleLargeSemiBold
                )
            }
        }
    }
}