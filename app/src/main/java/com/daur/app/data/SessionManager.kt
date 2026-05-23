package com.daur.app.data

/**
 * Menyimpan token & userId sesi aktif di memori.
 * Di-set saat login berhasil, di-clear saat logout.
 */
object SessionManager {
    var accessToken: String = ""
    var userId: String = ""
    val isLoggedIn: Boolean get() = accessToken.isNotEmpty()

    fun clear() {
        accessToken = ""
        userId = ""
    }
}