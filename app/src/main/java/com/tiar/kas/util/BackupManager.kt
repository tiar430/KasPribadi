package com.tiar.kas.util

import android.content.Context
import android.net.Uri
import com.tiar.kas.data.AppDatabase
import java.io.BufferedOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BackupManager {

    const val DB_ENTRY = "kas.db"
    const val PHOTO_FOLDER = "receipts/"

    private fun prefs(context: Context) = context.getSharedPreferences("backup", Context.MODE_PRIVATE)

    fun lastBackup(context: Context): Long = prefs(context).getLong("last_backup", 0L)

    // tulis ZIP ke lokasi pilihan, hasilnya jumlah foto yang ikut tersimpan
    suspend fun backup(context: Context, db: AppDatabase, target: Uri): Int = withContext(Dispatchers.IO) {
        // pindahkan isi kas.db-wal ke kas.db supaya satu file sudah lengkap
        db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").use { it.moveToFirst() }

        val dbFile = context.getDatabasePath("kas.db")
        val photos = File(context.filesDir, "receipts").listFiles().orEmpty()   // orEmpty kalau folder belum ada

        val output = context.contentResolver.openOutputStream(target)
            ?: error("Lokasi backup tidak bisa ditulis")       // error() melempar exception dengan pesan ini

        ZipOutputStream(BufferedOutputStream(output)).use { zip ->
            zip.putNextEntry(ZipEntry(DB_ENTRY))               // mulai satu file di dalam ZIP
            dbFile.inputStream().use { it.copyTo(zip) }
            zip.closeEntry()

            photos.forEach { photo ->
                zip.putNextEntry(ZipEntry(PHOTO_FOLDER + photo.name))
                photo.inputStream().use { it.copyTo(zip) }
                zip.closeEntry()
            }
        }

        prefs(context).edit().putLong("last_backup", System.currentTimeMillis()).apply()
        photos.size
    }
}
