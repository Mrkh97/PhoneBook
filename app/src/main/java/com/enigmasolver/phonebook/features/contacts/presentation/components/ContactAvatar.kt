package com.enigmasolver.phonebook.features.contacts.presentation.components

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.enigmasolver.phonebook.shared.theme.AppColors
import com.enigmasolver.phonebook.shared.theme.AppTypography
import android.graphics.Color as AndroidColor

@Composable
fun ContactAvatar(
    imageUrl: String?,
    name: String,
    onMainColorLoaded: (Color) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val placeholder: @Composable () -> Unit = {
        val char = name.firstOrNull()?.uppercaseChar() ?: '?'

        Box(
            modifier = modifier
                .size(40.dp)
                .background(AppColors.Blue50, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = char.toString(),
                color = AppColors.BluePrimary,
                style = AppTypography.titleMediumBold,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (imageUrl.isNullOrBlank()) {
        placeholder()
    } else {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .allowHardware(false)
                .listener(
                    onSuccess = { _, result ->
                        val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
                        if (bitmap != null) {
                            Palette.from(bitmap).generate { palette ->
                                val dominantInt =
                                    palette?.getVibrantColor(AndroidColor.TRANSPARENT)

                                if (dominantInt != null && dominantInt != AndroidColor.TRANSPARENT) {
                                    onMainColorLoaded(Color(dominantInt))
                                }
                            }
                        }
                    }
                )
                .build(),
            contentDescription = "$name's Avatar",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(CircleShape)
                .size(40.dp),
            loading = {
                placeholder()
            },
            error = {
                placeholder()
            }
        )
    }
}
