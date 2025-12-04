package com.enigmasolver.phonebook.shared.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.enigmasolver.phonebook.R
import com.enigmasolver.phonebook.shared.theme.AppColors
import com.enigmasolver.phonebook.shared.theme.AppTypography

@Composable
fun SearchInput(
    placeholder: String = "",
    modifier: Modifier = Modifier,
    initialValue: String = "",
    onValueChange: (String) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val state = rememberTextFieldState(initialValue)
    LaunchedEffect(state.text) {
        onValueChange(state.text.toString())
    }
    LaunchedEffect(initialValue) {
        state.setTextAndPlaceCursorAtEnd(initialValue)
    }
    TextField(
        state = state,
        leadingIcon =
            {
                Image(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        color = AppColors.Gray300
                    )
                )
            },
        placeholder = {
            Text(
                placeholder,
                style = AppTypography.titleMediumSemiBold.copy(color = AppColors.Gray300)
            )
        },
        shape = RoundedCornerShape(8.dp),
        textStyle = AppTypography.titleMediumBold.copy(color = AppColors.Gray950),
        contentPadding = PaddingValues(horizontal = 16.dp),
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
        onKeyboardAction = {
            focusManager.clearFocus()
        }
    )
}