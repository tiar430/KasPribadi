package com.tiar.kas.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.data.DB_VERSION
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BackupManager {

    const val DB_ENTRY = "kas.db"
    const val PHOTO_FOLDER = "receipts/"

    private fun prefs(context: Context) = context.getSharedPreferences("backup", Context.MODE_PRIVATE)

    fun lastBackup(context: Context): Long = prefs(context).getLong("last_backup", 0L)

    // 0 berarti pengingat mati
    fun reminderDays(context: Context): Int = prefs(context).getInt("reminder_days", 7)
    fun setReminderDays(context: Context, days: Int) {
    	prefs(context).edit().putInt("reminder_days", days).apply()
    }

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

    suspend fun restore(context: Context, source: Uri) = withContext(Dispatchers.IO) {
    	val work = File(context.cacheDir, "restore").apply {
    		deleteRecursively()
    		mkdirs()
    	}
    	val photoDir = File(work, "receipts").apply { mkdirs() }
    	var dbFound = false

    	val input = context.contentResolver.openInputStream(source)
    		?: error("File backup tidak bisa dibuka")

    	ZipInputStream(BufferedInputStream(input)).use { zip ->
    		var entry = zip.nextEntry
    		while (entry != null) {
    			val name = entry.name
    			val target = when {
    				name == DB_ENTRY -> File(work, DB_ENTRY).also { dbFound = true }
    				name.startsWith(PHOTO_FOLDER) && !entry.isDirectory ->
    					File(photoDir, File(name).name)
    				else -> null
    			}
    			if (target != null) FileOutputStream(target).use { zip.copyTo(it) }
    			entry = zip.nextEntry
    		}
    	}
    	if (!dbFound) error("File ini bukan backup Kas Pribadi")

    	val restoreDb = File(work, DB_ENTRY)
    	val version = readUserVersion(restoreDb) ?: error("Database di backup rusak")
    	if (version !in 3..DB_VERSION) error("Backup versi $version tidak didukung aplikasi ini")

    	AppDatabase.closeAndReset()
    	val dbFile = context.getDatabasePath("kas.db")
    	File(dbFile.path + "-wal").delete()
    	File(dbFile.path + "-shm").delete()
    	restoreDb.copyTo(dbFile, overwrite = true)

    	val liveDir = File(context.filesDir, "receipts").apply { mkdirs() }
    	liveDir.listFiles()?.forEach { it.delete() }
    	photoDir.listFiles()?.forEach { it.copyTo(File(liveDir, it.name), overwrite = true) }
    	work.deleteRecursively()
    }

    private fun readUserVersion(file: File): Int? = RandomAccessFile(file, "r").use { raf ->
    	if (raf.length() < 100) return null
    	val magic = ByteArray(16)
    	raf.readFully(magic)
    	if (String(magic, Charsets.US_ASCII) != "SQLite format 3\u0000") return null
    	raf.seek(60)
    	raf.readInt()
    }

    fun restartApp(context: Context) {
    	val launch = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return
    	context.startActivity(Intent.makeRestartActivityTask(launch.component))
    	Runtime.getRuntime().exit(0)
    }
}
