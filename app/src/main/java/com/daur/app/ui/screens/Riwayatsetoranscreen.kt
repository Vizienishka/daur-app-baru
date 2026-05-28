package com.daur.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.daur.app.model.Setoran
import com.daur.app.ui.theme.*
import com.daur.app.viewmodel.RiwayatViewModel
import com.daur.app.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatSetoranScreen(vm: RiwayatViewModel = viewModel(), onDeleted: () -> Unit = {}) {
    val state by vm.state.collectAsState()
    val selectedFilter by vm.selectedFilter.collectAsState()
    val deleteState by vm.deleteState.collectAsState()
    var expandedId by remember { mutableStateOf<String?>(null) }
    var confirmDeleteId by remember { mutableStateOf<String?>(null) }  // ID yang mau dihapus

    // Reload data setiap kali screen ini dibuka/ditampilkan
    LaunchedEffect(Unit) { vm.load() }

    // ── Snackbar untuk hasil delete ────────────────────────
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(deleteState) {
        when (val s = deleteState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar("✅ Setoran berhasil dihapus")
                vm.resetDelete()
                onDeleted()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar("❌ ${s.message}")
                vm.resetDelete()
            }
            else -> {}
        }
    }

    // ── Confirmation dialog ────────────────────────────────
    confirmDeleteId?.let { idToDelete ->
        AlertDialog(
            onDismissRequest = { confirmDeleteId = null },
            icon = {
                Icon(
                    Icons.Outlined.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Hapus Setoran?", fontWeight = FontWeight.SemiBold) },
            text  = {
                Text(
                    "Data setoran ini akan dihapus permanen dan tidak bisa dikembalikan.",
                    fontSize = 14.sp, color = OnSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.deleteSetoran(idToDelete)
                        confirmDeleteId = null
                        expandedId = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { confirmDeleteId = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // FIX: ganti Scaffold dengan Column biasa supaya tidak double padding
    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        TopAppBar(
            title = { Text("Riwayat", fontWeight = FontWeight.Bold, color = Primary, fontSize = 20.sp) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
        )

        // ── Loading overlay saat delete sedang diproses ───
        if (deleteState is UiState.Loading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color    = Primary
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // ── Header ──────────────────────────────────
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Text("Aktivitas Setoran", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("Lacak kontribusi lingkungan kamu.", fontSize = 14.sp, color = OnSurfaceVariant)
                    }
                }

                // ── Filter chips ─────────────────────────────
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        items(vm.filters) { filter ->
                            val isSelected = selectedFilter == filter
                            val label = vm.filterLabels[filter] ?: filter
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) Primary else SurfaceContainer)
                                    .border(1.dp, if (isSelected) Color.Transparent else OutlineVariant, CircleShape)
                                    .clickable { vm.setFilter(filter) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else OnSurfaceVariant)
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
                            icon    = Icons.Outlined.Inbox,
                            title   = "Belum ada riwayat",
                            message = "Mulai setor sampah pertamamu dan jejak kontribusimu akan muncul di sini."
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
                    is UiState.Success -> items(s.data, key = { it.id }) { setoran ->
                        SetoranCard(
                            setoran    = setoran,
                            isExpanded = expandedId == setoran.id,
                            onToggle   = { expandedId = if (expandedId == setoran.id) null else setoran.id },
                            onDelete   = { confirmDeleteId = setoran.id }
                        )
                    }
                }
            }
        } // end Box
        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun SetoranCard(
    setoran: Setoran,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val rotate by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(200), label = "rotate"
    )

    // Warna status
    val (statusColor, statusBg) = when (setoran.status) {
        "selesai"  -> Primary to Primary.copy(alpha = 0.1f)
        "diproses" -> Secondary to Secondary.copy(alpha = 0.1f)
        "ditolak"  -> Error to Error.copy(alpha = 0.1f)
        else       -> OnSurfaceVariant to SurfaceContainer
    }
    val statusLabel = when (setoran.status) {
        "selesai"  -> "Selesai"
        "diproses" -> "Diproses"
        "ditolak"  -> "Ditolak"
        else       -> "Menunggu"
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp).clickable { onToggle() },
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Main row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Icon status
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(statusBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (setoran.status == "selesai") Icons.Outlined.CheckCircle
                            else Icons.Outlined.Recycling,
                            contentDescription = null, tint = statusColor, modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = setoran.createdAt.take(10).replace("-", "/").ifEmpty { "—" },
                            fontSize = 11.sp, color = OnSurfaceVariant
                        )
                        Text(setoran.kodeSetoran, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("%.1f kg".format(setoran.totalBerat), fontSize = 13.sp, color = Color(0xFF6D7A73))
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    if (setoran.totalPoin > 0)
                        Text("+${setoran.totalPoin} Pts", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Primary)
                    else
                        Text("— Pts", fontSize = 15.sp, color = OnSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.ExpandMore, contentDescription = null,
                            tint = OnSurfaceVariant, modifier = Modifier.size(22.dp).rotate(rotate))
                        Spacer(Modifier.width(4.dp))
                        // ── Tombol Delete ──────────────────
                        IconButton(
                            onClick  = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = "Hapus setoran",
                                tint     = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Expanded detail
            AnimatedVisibility(
                visible = isExpanded,
                enter   = expandVertically() + fadeIn(),
                exit    = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Foto placeholder
                        // SESUDAH — tampil foto jika ada, placeholder jika tidak ✅
                        if (setoran.fotoUrl.isNotEmpty()) {
                            AsyncImage(
                                model              = setoran.fotoUrl,
                                contentDescription = "Foto sampah",
                                contentScale       = ContentScale.Crop,
                                modifier           = Modifier
                                    .weight(1f)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier.weight(1f).height(100.dp)
                                    .clip(RoundedCornerShape(12.dp)).background(SurfaceContainer),
                                contentAlignment = Alignment.Center
                            ) { Icon(Icons.Outlined.Image, contentDescription = null, tint = Color(0xFF6D7A73), modifier = Modifier.size(36.dp)) }
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetailBox(label = "STATUS", value = statusLabel, valueColor = statusColor, bg = statusBg)
                            if (setoran.totalHarga > 0)
                                DetailBox(label = "NILAI", value = "Rp %,.0f".format(setoran.totalHarga), valueColor = Secondary, bg = Secondary.copy(alpha = 0.08f))
                            if (setoran.catatan.isNotEmpty() && setoran.catatan != "null")
                                DetailBox(label = "CATATAN", value = setoran.catatan, valueColor = OnSurface, bg = Color(0xFFF3F4F5))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailBox(label: String, value: String, valueColor: Color, bg: Color) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(bg).padding(8.dp)
    ) {
        Column {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D7A73), letterSpacing = 0.5.sp)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
        }
    }
}