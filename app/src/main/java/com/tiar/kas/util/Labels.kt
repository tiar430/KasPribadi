package com.tiar.kas.util

import com.tiar.kas.data.AccountGroup

// extension function, dipanggil seperti method biasa, contoh AccountGroup.E_WALLET.label()
fun AccountGroup.label(): String = when (this) {     // this adalah nilai enum yang memanggil
    AccountGroup.TUNAI -> "Tunai"
    AccountGroup.BANK -> "Bank"
    AccountGroup.E_WALLET -> "E-Wallet"
    AccountGroup.KARTU_KREDIT -> "Kartu Kredit"
    AccountGroup.INVESTASI -> "Investasi"
    AccountGroup.PINJAMAN -> "Pinjaman"
}
