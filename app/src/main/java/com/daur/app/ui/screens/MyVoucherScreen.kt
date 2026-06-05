package com.daur.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daur.app.model.PenukaranPoin
import com.daur.app.ui.components.VoucherDetailSheet
import com.daur.app.ui.theme.*
import com.daur.app.viewmodel.MyVoucherViewModel
import com.daur.app.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyVoucherScreen(
    onBack: () -> Unit,
    vm: MyVoucherViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val hapusState by vm.hapusState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedVoucher by remember { mutableStateOf<PenukaranPoin?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(hapusState) {
        when (val s = hapusState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar("Voucher berhasil digunakan")
                selectedVoucher = null
                vm.resetHapus()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(s.message)
                vm.resetHapus()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Voucher Saya", fontWeight = FontWeight.Bold, color = Primary, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )

            when (val s = state) {
                is UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
                is UiState.Empty -> EmptyState(
                    icon = Icons.Outlined.LocalOffer,
                    title = "Belum ada voucher",
                    message = "Kamu belum menukarkan poin dengan voucher."
                )
                is UiState.Error -> EmptyState(
                    icon = Icons.Outlined.ErrorOutline,
                    title = "Gagal memuat",
                    message = s.message,
                    isError = true,
                    onRetry = { vm.load() }
                )
                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(s.data) { item ->
                            MyVoucherCard(
                                penukaranPoin = item,
                                onDetail = { selectedVoucher = item }
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }

    if (selectedVoucher != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedVoucher = null },
            sheetState = sheetState,
            containerColor = Color.Transparent,
            dragHandle = null
        ) {
            VoucherDetailSheet(
                penukaranPoin = selectedVoucher!!,
                onDismiss = { selectedVoucher = null },
                onGunakan = { 
                    vm.hapus(selectedVoucher!!.id)
                    selectedVoucher = null
                },
                gunakanState = hapusState
            )
        }
    }
}

@Composable
fun MyVoucherCard(penukaranPoin: PenukaranPoin, onDetail: () -> Unit) {
    val reward = penukaranPoin.reward ?: return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Secondary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.LocalOffer, contentDescription = null, tint = Secondary, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(reward.nama, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Stars, contentDescription = null, tint = Color(0xFFFCAA33), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${penukaranPoin.poinDigunakan} Poin", fontSize = 14.sp, color = OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Kode: ${penukaranPoin.kodeTukar}", fontSize = 12.sp, color = Primary, fontWeight = FontWeight.SemiBold)
                }
            }
            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
            TextButton(
                onClick = onDetail,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = Primary)
            ) {
                Text("Lihat Detail", fontWeight = FontWeight.Bold)
            }
        }
    }
}
