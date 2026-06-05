package com.daur.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.daur.app.ui.theme.*

data class BottomNavItem(
    val route: String,
    val label: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector
)

// Profil dihapus dari nav — diakses via avatar di Beranda
val bottomNavItems = listOf(
    BottomNavItem("beranda", "Beranda", Icons.Filled.Home,     Icons.Outlined.Home),
    BottomNavItem("riwayat", "Riwayat", Icons.Filled.History,  Icons.Outlined.History),
    BottomNavItem("setor",   "Setor",   Icons.Filled.AddCircle, Icons.Outlined.AddCircle),
    BottomNavItem("hadiah",  "Hadiah",  Icons.Filled.Redeem,   Icons.Outlined.Redeem),
    BottomNavItem("edukasi", "Edukasi", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
)

@Composable
fun BottomNavBar(currentRoute: String, onItemClick: (String) -> Unit) {
    // Tinggi navbar + extra space untuk FAB yang menonjol
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // ── Background navbar ──────────────────────────────
        Surface(
            modifier        = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color           = Surface,
            shadowElevation = 12.dp,
            shape           = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(64.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    if (item.route == "setor") {
                        // Spacer kosong untuk slot FAB di tengah
                        Spacer(modifier = Modifier.width(64.dp))
                    } else {
                        NavItemView(
                            item       = item,
                            isSelected = currentRoute == item.route,
                            onClick    = { onItemClick(item.route) }
                        )
                    }
                }
            }
        }

        // ── FAB Setor mengambang di atas navbar ────────────
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f)
                .offset(y = (-22).dp)
        ) {
            SetorFabButton(
                isSelected = currentRoute == "setor",
                onClick    = { onItemClick("setor") }
            )
        }
    }
}

// ── FAB Setor menonjol ─────────────────────────────────────
@Composable
private fun SetorFabButton(isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick           = onClick
            )
    ) {
        // Lingkaran putih sebagai "ring" / border di luar FAB
        Box(
            modifier = Modifier
                .size(68.dp)
                .shadow(
                    elevation    = 10.dp,
                    shape        = CircleShape,
                    ambientColor = Primary.copy(alpha = 0.25f),
                    spotColor    = Primary.copy(alpha = 0.4f)
                )
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            // FAB utama
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Primary, Color(0xFF004D38))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.Add,
                    contentDescription = "Setor Sampah",
                    tint               = Color.White,
                    modifier           = Modifier.size(30.dp)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text       = "Setor",
            fontSize   = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color      = if (isSelected) Primary else OnSurfaceVariant
        )
    }
}

// ── Nav item biasa ─────────────────────────────────────────
@Composable
private fun NavItemView(item: BottomNavItem, isSelected: Boolean, onClick: () -> Unit) {
    val contentColor by animateColorAsState(
        targetValue   = if (isSelected) Primary else OnSurfaceVariant,
        animationSpec = tween(200), label = "navContent"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick           = onClick
            )
            .padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector        = if (isSelected) item.iconSelected else item.iconUnselected,
            contentDescription = item.label,
            tint               = contentColor,
            modifier           = Modifier.size(22.dp)
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text       = item.label,
            fontSize   = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color      = contentColor
        )
    }
}