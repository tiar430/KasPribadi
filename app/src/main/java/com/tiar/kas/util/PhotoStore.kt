package com.tiar.kas.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// object berarti hanya ada satu PhotoStore di seluruh aplikasi, tanpa perlu dibuat dengan PhotoStore()
object PhotoStore {
	private const val MAX_SIDE = 1600
	private fun dir(context: Context): File = File(context.filesDir, "receipts").apply { mkdirs() }
	fun fileOf(context: Context, name: String): File = File(dir(context), name)

	// salin foto dari galeri ke folder aplikasi, hasilya nama file atau null kalau gagal
	suspend fun import(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
		val resolver = context.contentResolver

		// baca ukuran asli tanpa memuat seluruh gambar ke memori
		val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
		resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
		if (bounds.outWidth <= 0) return@withContext null

		// pengecil kelipatan 2, berhenti sebelum sisi terpanjang di bawah MAX_SIDE
		val options = BitmapFactory.Options().apply { inSampleSize = sampleSize(bounds, MAX_SIDE) }
		val decoded = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
			?: return@withContext null

		// foto kamera sering menyimpan arah putar di data EXIF, bukan di pikselnya
		val rotation = resolver.openInputStream(uri)?.use { exifRotation(it) } ?: 0
		val upright =
			if (rotation == 0) decoded
			else Bitmap.createBitmap(
				decoded, 0, 0, decoded.width, decoded.height,
				Matrix().apply { postRotate(rotation.toFloat()) }, true
			)

		val name = "${UUID.randomUUID()}.jpg"	// nama acak agar tidak bentrok
		FileOutputStream(fileOf(context, name)).use { upright.compress(Bitmap.CompressFormat.JPEG, 80, it) }
		name
	}

	// baca foto yang sudah tersimpan, diperkecil sesuai kebutuhan tampilan
	suspend fun load(context: Context, name: String, maxSide: Int): Bitmap? = withContext(Dispatchers.IO) {
		val path = fileOf(context, name).absolutePath
		val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
		BitmapFactory.decodeFile(path, bounds)
		if (bounds.outWidth <= 0) return@withContext null
		BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sampleSize(bounds, maxSide) })
	}

	fun delete(context: Context, name: String?) {
		if (name != null) fileOf(context, name).delete()
	}

	fun newCameraTarget(context: Context): Pair<String, Uri> {
		val dir = File(context.cacheDir, "camera").apply { mkdirs() }
		val name = "${UUID.randomUUID()}.jpg"
		val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(dir, name))
		return name to uri
	}
	suspend fun cleanup(context: Context, used: Set<String>) = withContext(Dispatchers.IO) {
		val dayAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
		dir(context).listFiles()?.forEach { file ->
			if (file.name !in used && file.lastModified() < dayAgo) file.delete()
		}
		File(context.cacheDir, "camera").listFiles()?.forEach { file ->
			if (file.lastModified() < dayAgo) file.delete()
		}
	}

	private fun sampleSize(bounds: BitmapFactory.Options, maxSide: Int): Int {
		var sample = 1
		while (bounds.outWidth / (sample * 2) >= maxSide || bounds.outHeight / (sample * 2) >= maxSide) {
			sample *= 2
		}
		return sample
	}

	private fun exifRotation(input: InputStream): Int =
		when (ExifInterface(input).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
			ExifInterface.ORIENTATION_ROTATE_90 -> 90
			ExifInterface.ORIENTATION_ROTATE_180 -> 180
			ExifInterface.ORIENTATION_ROTATE_270 -> 270
			else -> 0
		}
}
