package com.enigmasolver.phonebook.shared.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.enigmasolver.phonebook.shared.theme.AppColors
import com.enigmasolver.phonebook.shared.theme.AppTypography

@Composable
fun OutlinedInput(
    placeholder: String = "",
    initialValue: String = "",
    readOnly: Boolean = false,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val state = rememberTextFieldState(initialValue)
    LaunchedEffect(state.text) {
        onValueChange(state.text.toString())
    }
    OutlinedTextField(
        state = state,
        placeholder = {
            Text(
                placeholder,
                style = AppTypography.titleMediumSemiBold.copy(color = AppColors.Gray400)
            )
        },
        readOnly = readOnly,
        shape = RoundedCornerShape(8.dp),
        textStyle = AppTypography.titleMediumBold.copy(color = AppColors.Gray950),
        contentPadding = PaddingValues(horizontal = 16.dp),
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = AppColors.Gray400,
            unfocusedIndicatorColor = AppColors.Gray100,
            disabledIndicatorColor = AppColors.Gray50,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
        onKeyboardAction = {
            focusManager.clearFocus()
        }
    )
}