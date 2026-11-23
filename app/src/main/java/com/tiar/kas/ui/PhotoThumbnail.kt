package com.tiar.kas.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.tiar.kas.util.PhotoStore

@Composable
fun PhotoThumbnail(
	name: String,
	maxSide: Int = 400,
	contentScale: ContentScale = ContentScale.Crop,
	modifier: Modifier = Modifier
) {
	val context = LocalContext.current

	// produceState menjalankan coroutine dan menyimpan hasilnya sebagai state, diulang kalau nama berubah
	val bitmap by produceState<Bitmap?>(initialValue = null, name) {
		value = PhotoStore.load(context, name, maxSide)
	}

	bitmap?.let {
		Image(
			bitmap = it.asImageBitmap(),		// Bitmap Android dirubah ke format gambar Compose
			contentDescription = "Foto struk",
			contentScale = contentScale,
			modifier = modifier
		)
	}
}
