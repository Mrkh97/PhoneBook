package com.enigmasolver.phonebook.shared.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.enigmasolver.phonebook.R
import com.enigmasolver.phonebook.shared.theme.AppColors
import com.enigmasolver.phonebook.shared.theme.AppTypography

@Composable
fun SuccessToast(message: String) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),

        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(56.dp)
            .dropShadow(
                shadow = androidx.compose.ui.graphics.shadow.Shadow(
                    color = Color.Black.copy(alpha = 0.08f),
                    radius = 16.dp
                ),
                shape = RoundedCornerShape(16.dp),
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.done),
                contentDescription = "Done Icon",
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = message,
                color = AppColors.Success, // Green text
                style = AppTypography.titleMediumBold
            )
        }
    }
}