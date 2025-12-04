package com.enigmasolver.phonebook.shared.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val LightColorScheme = lightColorScheme(
    primary = AppColors.BluePrimary,
    secondary = AppColors.Gray500,
    tertiary = AppColors.Gray400,
    surface = Color.White,
    background = AppColors.Gray50,
    onPrimary = AppColors.Gray50,
    onBackground = AppColors.Gray950,
    onSurface = AppColors.Gray900,
    onSurfaceVariant = AppColors.Gray500,
    error = AppColors.RedDelete,
    surfaceContainer = AppColors.Gray50,
    surfaceContainerLow = Color.White,
    surfaceContainerLowest = Color.White,
    surfaceContainerHigh = AppColors.Gray100,
    surfaceContainerHighest = AppColors.Gray200,


    /* Other default colors to override
    onSecondary = Color.White,
    onTertiary = Color.White,
    */
)

@Composable
fun PhoneBookTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}