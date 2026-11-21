package com.tiar.kas.util

import com.tiar.kas.data.AccountGroup
import com.tiar.kas.data.Frequency

// extension function, dipanggil seperti method biasa, contoh AccountGroup.E_WALLET.label()
fun AccountGroup.label(): String = when (this) {     // this adalah nilai enum yang memanggil
    AccountGroup.TUNAI -> "Tunai"
    AccountGroup.BANK -> "Bank"
    AccountGroup.E_WALLET -> "E-Wallet"
    AccountGroup.KARTU_KREDIT -> "Kartu Kredit"
    AccountGroup.INVESTASI -> "Investasi"
    AccountGroup.PINJAMAN -> "Pinjaman"
}

fun Frequency.label(): String = when (this) {
	Frequency.HARIAN -> "Setiap hari"
	Frequency.MINGGUAN -> "Setiap minggu"
	Frequency.BULANAN -> "Setiap bulan"
	Frequency.TAHUNAN -> "Setiap tahun"
}
