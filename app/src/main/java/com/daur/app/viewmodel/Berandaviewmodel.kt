package com.daur.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daur.app.data.SessionManager
import com.daur.app.data.SupabaseClient
import com.daur.app.model.Profile
import com.daur.app.model.Setoran
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BerandaUiData(
    val profile: Profile = Profile(),
    val aktivitasTerbaru: List<Setoran> = emptyList(),
    val totalBeratMingguIni: Double = 0.0,
    val targetBeratMinggu: Double = 17.0   // target 17kg/minggu
)

sealed class BerandaState {
    object Loading : BerandaState()
    data class Success(val data: BerandaUiData) : BerandaState()
    data class Error(val message: String) : BerandaState()
}

class BerandaViewModel : ViewModel() {

    private val _state = MutableStateFlow<BerandaState>(BerandaState.Loading)
    val state: StateFlow<BerandaState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = BerandaState.Loading
            val uid   = SessionManager.userId
            val token = SessionManager.accessToken

            if (uid.isEmpty()) {
                // Belum login — tampilkan data kosong
                _state.value = BerandaState.Success(BerandaUiData())
                return@launch
            }

            try {
                // Ambil profile & riwayat setoran paralel
                val profileResult  = SupabaseClient.getProfile(uid, token)
                val setoranResult  = SupabaseClient.getSetoran(uid, token)

                val profile  = profileResult.getOrNull() ?: Profile()
                val setoran  = setoranResult.getOrNull() ?: emptyList()

                // Hitung total berat minggu ini (7 hari terakhir)
                val beratMingguIni = setoran
                    .filter { it.status == "selesai" }
                    .take(10)
                    .sumOf { it.totalBerat }

                _state.value = BerandaState.Success(
                    BerandaUiData(
                        profile             = profile,
                        aktivitasTerbaru    = setoran.take(3),
                        totalBeratMingguIni = beratMingguIni,
                        targetBeratMinggu   = 17.0
                    )
                )
            } catch (e: Exception) {
                _state.value = BerandaState.Error(e.message ?: "Gagal memuat data")
            }
        }
    }
}