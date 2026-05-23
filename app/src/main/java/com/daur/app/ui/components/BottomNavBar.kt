package com.daur.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daur.app.ui.theme.*

data class BottomNavItem(
    val route: String,
    val label: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("beranda", "Beranda",  Icons.Filled.Home,       Icons.Outlined.Home),
    BottomNavItem("riwayat", "Riwayat",  Icons.Filled.History,    Icons.Outlined.History),
    BottomNavItem("setor",   "Setor",    Icons.Filled.AddCircle,  Icons.Outlined.AddCircle),
    BottomNavItem("hadiah",  "Hadiah",   Icons.Filled.Redeem,     Icons.Outlined.Redeem),
    BottomNavItem("edukasi", "Edukasi",  Icons.Filled.MenuBook,   Icons.Outlined.MenuBook),
    BottomNavItem("profil",  "Profil",   Icons.Filled.Person,     Icons.Outlined.Person)
)

@Composable
fun BottomNavBar(currentRoute: String, onItemClick: (String) -> Unit) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        color           = Surface,
        shadowElevation = 8.dp,
        shape           = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                NavItemView(
                    item       = item,
                    isSelected = currentRoute == item.route,
                    onClick    = { onItemClick(item.route) }
                )
            }
        }
    }
}

@Composable
private fun NavItemView(item: BottomNavItem, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) PrimaryContainer else androidx.compose.ui.graphics.Color.Transparent,
        animationSpec = tween(200), label = "navBg"
    )
    val contentColor by animateColorAsState(
        targetValue   = if (isSelected) Primary else OnSurfaceVariant,
        animationSpec = tween(200), label = "navContent"
    )
    val hPadding by animateDpAsState(
        targetValue   = if (isSelected) 12.dp else 0.dp,
        animationSpec = tween(200), label = "navPad"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick           = onClick
            )
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(bgColor)
                .padding(horizontal = hPadding, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = if (isSelected) item.iconSelected else item.iconUnselected,
                contentDescription = item.label,
                tint               = contentColor,
                modifier           = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text       = item.label,
            fontSize   = 9.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color      = contentColor
        )
    }
}