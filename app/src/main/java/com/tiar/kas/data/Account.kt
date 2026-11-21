package com.tiar.kas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// jenis akun, disimpan Room sebagai teks
enum class AccountGroup { TUNAI, BANK, E_WALLET, KARTU_KREDIT, INVESTASI, PINJAMAN }

@Entity(tableName = "accounts")			// class ini menjadi tabel accounts
data class Account(
	@PrimaryKey(autoGenerate = true)	// id otomatis bertambah 1, 2, 3
	val id: Long = 0,
	val name: String,					// contoh BCA atau Dompet
	val accountGroup: AccountGroup,
	val initialBalance: Long = 0,		// saldo awal dalam rupiah
	val includeInTotal: Boolean = true,	// ikut dihitung di total aset atau tidak
	val sortOrder: Int = 0				// urutan tampil di daftar
)
