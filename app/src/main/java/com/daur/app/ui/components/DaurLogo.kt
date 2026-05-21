package com.daur.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.daur.app.ui.theme.Primary

// Logo DAUR (daun/tetesan air) digambar via vector path
val DaurLogoVector: ImageVector
    get() = ImageVector.Builder(
        name            = "DaurLogo",
        defaultWidth   = 100.dp,
        defaultHeight  = 100.dp,
        viewportWidth  = 100f,
        viewportHeight = 100f
    ).apply {
        // Bentuk tetesan air / daun
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.White)) {
            moveTo(50f, 5f)
            curveTo(50f, 5f, 15f, 40f, 15f, 62f)   // ✅ cubicTo → curveTo
            curveTo(15f, 80f, 31f, 95f, 50f, 95f)   // ✅
            curveTo(69f, 95f, 85f, 80f, 85f, 62f)   // ✅
            curveTo(85f, 40f, 50f, 5f, 50f, 5f)     // ✅
            close()
        }
        // Garis tengah (batang)
        path(fill = androidx.compose.ui.graphics.SolidColor(Primary)) {
            moveTo(48f, 30f)
            lineTo(52f, 30f)
            lineTo(52f, 85f)
            lineTo(48f, 85f)
            close()
        }
        // Cabang kiri
        path(fill = androidx.compose.ui.graphics.SolidColor(Primary)) {
            moveTo(50f, 50f)
            lineTo(28f, 35f)
            lineTo(30f, 32f)
            lineTo(50f, 46f)
            close()
        }
        // Cabang kanan
        path(fill = androidx.compose.ui.graphics.SolidColor(Primary)) {
            moveTo(50f, 50f)
            lineTo(72f, 35f)
            lineTo(70f, 32f)
            lineTo(50f, 46f)
            close()
        }
    }.build()

@Composable
fun DaurLogo(
    modifier: Modifier = Modifier.size(80.dp),
    tint: Color = Primary
) {
    Icon(
        imageVector = DaurLogoVector,
        contentDescription = "DAUR Logo",
        tint = Color.Unspecified, // pakai warna dari vector
        modifier = modifier
    )
}
