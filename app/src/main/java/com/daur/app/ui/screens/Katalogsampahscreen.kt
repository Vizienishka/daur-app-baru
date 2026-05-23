package com.daur.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daur.app.model.KatalogSampah
import com.daur.app.ui.theme.*
import com.daur.app.viewmodel.KatalogViewModel
import com.daur.app.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KatalogSampahScreen(vm: KatalogViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val selectedFilter by vm.selectedFilter.collectAsState()
    val searchQuery by vm.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Katalog Sampah", fontWeight = FontWeight.Bold, color = Primary, fontSize = 20.sp) },
                actions = {
                    IconButton(onClick = { vm.load() }) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Refresh", tint = OnSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        containerColor = Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ── Search Bar ───────────────────────────────
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { vm.setSearch(it) },
                    placeholder = { Text("Cari jenis sampah...", color = Outline) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Outline) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { vm.setSearch("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Hapus", tint = Outline)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Primary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor   = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            // ── Filter Chips ─────────────────────────────
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(vm.filters) { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) Primary else SurfaceContainer)
                                .clickable { vm.setFilter(filter) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                filter.replaceFirstChar { it.uppercase() },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ── Content ───────────────────────────────────
            when (val s = state) {
                is UiState.Loading -> item {
                    Box(Modifier.fillMaxWidth().padding(top = 64.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is UiState.Empty -> item {
                    EmptyState(
                        icon    = Icons.Outlined.SearchOff,
                        title   = "Tidak ditemukan",
                        message = "Coba kata kunci atau filter lain."
                    )
                }
                is UiState.Error -> item {
                    EmptyState(
                        icon    = Icons.Outlined.ErrorOutline,
                        title   = "Gagal memuat",
                        message = s.message,
                        isError = true,
                        onRetry = { vm.load() }
                    )
                }
                is UiState.Success -> {
                    items(s.data, key = { it.id }) { katalog ->
                        KatalogCard(katalog = katalog)
                    }
                }
            }

            // ── Banner bawah ──────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFCAA33))
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .align(Alignment.CenterEnd)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    )
                    Column(modifier = Modifier.fillMaxWidth(0.72f)) {
                        Text("Sampah = Nilai", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4a2c00))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Setiap kg sampah yang kamu setor memberikan poin dan nilai nyata untuk bumi.",
                            fontSize = 12.sp,
                            color = Color(0xFF6b4200),
                            lineHeight = 18.sp
                        )
                    }
                    Icon(
                        Icons.Outlined.Eco,
                        contentDescription = null,
                        tint = Color(0xFF4a2c00).copy(alpha = 0.15f),
                        modifier = Modifier.size(72.dp).align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}

@Composable
private fun KatalogCard(katalog: KatalogSampah) {
    val (iconBg, iconTint, kategoriColor, kategoriBg) = when (katalog.kategori) {
        "plastik"    -> listOf(Primary.copy(0.1f), Primary,      Color(0xFF005234), Color(0xFF68dbae).copy(0.25f))
        "kertas"     -> listOf(Secondary.copy(0.1f), Secondary,  Color(0xFF653e00), Color(0xFFffddb7).copy(0.4f))
        "logam"      -> listOf(Color(0xFF41484a).copy(0.1f), Color(0xFF41484a), Color(0xFF41484a), Color(0xFFdde4e6).copy(0.5f))
        "kaca"       -> listOf(Color(0xFF006874).copy(0.1f), Color(0xFF006874), Color(0xFF006874), Color(0xFFa2eeff).copy(0.35f))
        "elektronik" -> listOf(Error.copy(0.1f), Error,          Color(0xFF93000a), Color(0xFFffdad6).copy(0.5f))
        else         -> listOf(Primary.copy(0.1f), Primary,      Primary,           Primary.copy(0.1f))
    }
    val icon: ImageVector = when (katalog.kategori) {
        "plastik"    -> Icons.Outlined.Inventory2
        "kertas"     -> Icons.Outlined.Description
        "logam"      -> Icons.Outlined.Hardware
        "kaca"       -> Icons.Outlined.WineBar
        "elektronik" -> Icons.Outlined.DevicesOther
        else         -> Icons.Outlined.Recycling
    }

    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        border    = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainer),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(iconBg as Color),
                contentAlignment = Alignment.Center
            ) { Icon(icon, contentDescription = null, tint = iconTint as Color, modifier = Modifier.size(28.dp)) }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(katalog.nama, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(kategoriBg as Color)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            katalog.kategori.replaceFirstChar { it.uppercase() },
                            fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = kategoriColor as Color
                        )
                    }
                }
                if (katalog.deskripsi.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(katalog.deskripsi, fontSize = 12.sp, color = OnSurfaceVariant, maxLines = 1)
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Harga
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Outlined.Payments, contentDescription = null, tint = Secondary, modifier = Modifier.size(14.dp))
                        Text("Rp %,.0f/kg".format(katalog.hargaPerKg), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Secondary)
                    }
                    // Poin
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.Stars, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                        Text("${katalog.poinPerKg} poin/kg", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Primary)
                    }
                }
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Outline)
        }
    }
}