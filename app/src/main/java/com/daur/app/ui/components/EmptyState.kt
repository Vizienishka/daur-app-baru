package com.daur.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daur.app.ui.theme.*

/**
 * Shared empty / error state composable.
 * Digunakan oleh semua screen.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    isError: Boolean = false,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = if (isError) Error.copy(alpha = 0.6f) else OnSurfaceVariant.copy(alpha = 0.4f),
            modifier           = Modifier.size(64.dp)
        )
        Text(
            text       = title,
            fontSize   = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color      = if (isError) Error else OnSurface,
            textAlign  = TextAlign.Center
        )
        Text(
            text       = message,
            fontSize   = 14.sp,
            color      = OnSurfaceVariant,
            textAlign  = TextAlign.Center,
            lineHeight = 20.sp
        )
        if (onRetry != null) {
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onRetry,
                shape   = CircleShape,
                colors  = ButtonDefaults.buttonColors(
                    containerColor = if (isError) Error else Primary
                )
            ) {
                Text("Coba Lagi", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}