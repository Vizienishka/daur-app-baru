package com.daur.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daur.app.model.KatalogSampah
import com.daur.app.ui.theme.*
import com.daur.app.viewmodel.SetorViewModel
import com.daur.app.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetorSampahScreen(vm: SetorViewModel = viewModel()) {
    val katalogState by vm.katalogState.collectAsState()
    val submitState  by vm.submitState.collectAsState()
    val selectedKatalog by vm.selectedKatalog.collectAsState()
    val berat by vm.berat.collectAsState()

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(submitState) {
        when (val s = submitState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar("✅ Setoran berhasil dikirim!")
                vm.resetSubmit()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar("❌ ${s.message}")
                vm.resetSubmit()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Setor Sampah", fontWeight = FontWeight.Bold, color = Primary, fontSize = 18.sp) },
                actions = {
                    IconButton(onClick = { vm.loadKatalog() }) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Refresh", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        containerColor = Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ── Step 1: Pilih Jenis Sampah ─────────────────
            item {
                StepSection(number = 1, title = "Pilih Jenis Sampah") {
                    when (val s = katalogState) {
                        is UiState.Loading -> {
                            Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Primary, modifier = Modifier.size(28.dp))
                            }
                        }
                        is UiState.Error -> {
                            Text(s.message, color = Error, fontSize = 13.sp)
                        }
                        is UiState.Success -> {
                            // LazyRow chip katalog
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(s.data) { katalog ->
                                    KatalogChip(
                                        katalog    = katalog,
                                        isSelected = selectedKatalog?.id == katalog.id,
                                        onClick    = { vm.selectedKatalog.value = katalog }
                                    )
                                }
                            }
                            // Info katalog terpilih
                            selectedKatalog?.let { kat ->
                                Spacer(Modifier.height(8.dp))
                                Card(
                                    shape  = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.06f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.15f)),
                                    elevation = CardDefaults.cardElevation(0.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(kat.nama, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                                            if (kat.deskripsi.isNotEmpty())
                                                Text(kat.deskripsi, fontSize = 12.sp, color = OnSurfaceVariant, maxLines = 2)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                "Rp %,.0f/kg".format(kat.hargaPerKg),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Secondary
                                            )
                                            Text(
                                                "${kat.poinPerKg} poin/kg",
                                                fontSize = 12.sp,
                                                color = Primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }

            // ── Step 2: Berat Sampah ──────────────────────
            item {
                StepSection(number = 2, title = "Berat Sampah (Estimasi)") {
                    Card(
                        shape  = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F5)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { vm.kurangBerat() },
                                modifier = Modifier.size(48.dp).clip(CircleShape).background(SurfaceContainer)
                            ) { Icon(Icons.Filled.Remove, contentDescription = "Kurang", tint = Primary) }

                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("%.1f".format(berat), fontSize = 40.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                Text("kg", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                            }

                            IconButton(
                                onClick = { vm.tambahBerat() },
                                modifier = Modifier.size(48.dp).clip(CircleShape).background(SurfaceContainer)
                            ) { Icon(Icons.Filled.Add, contentDescription = "Tambah", tint = Primary) }
                        }
                    }
                }
            }

            // ── Step 3: Foto Sampah ───────────────────────
            item {
                StepSection(number = 3, title = "Foto Sampah") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(2.dp, OutlineVariant, RoundedCornerShape(16.dp))
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = Outline, modifier = Modifier.size(48.dp))
                            Text(
                                "Ambil foto sampah yang ingin disetor\nuntuk verifikasi lebih cepat.",
                                fontSize = 13.sp, color = OnSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }
            }

            // ── Estimasi Poin ─────────────────────────────
            item {
                Card(
                    shape  = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.2f)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Stars, contentDescription = null, tint = Primary)
                            Text("Estimasi Poin", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier.clip(CircleShape).background(Primary).padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("%,d Pts".format(vm.estimasiPoin), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // ── Tombol Setor ──────────────────────────────
            item {
                val isLoading = submitState is UiState.Loading
                val isDisabled = katalogState !is UiState.Success || selectedKatalog == null

                Button(
                    onClick  = { vm.setor() },
                    enabled  = !isLoading && !isDisabled,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape    = CircleShape,
                    colors   = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Setor Sekarang", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun KatalogChip(katalog: KatalogSampah, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Primary else Color.White,
        animationSpec = tween(200), label = "chip_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else OnSurfaceVariant,
        animationSpec = tween(200), label = "chip_text"
    )
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, if (isSelected) Color.Transparent else OutlineVariant, CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = katalog.nama.split(" ").take(2).joinToString(" "),
            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textColor
        )
    }
}

@Composable
private fun StepSection(number: Int, title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(Primary),
                contentAlignment = Alignment.Center
            ) { Text(number.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White) }
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
        }
        Column { content() }
    }
}