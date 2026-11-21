package com.tiar.kas.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun PickerField(
	label: String,
	selectedText: String?,				// teks pilihan sekarang, null kalau belum dipilih
	options: List<Pair<Long, String>>,	// pasangan id dan teks yang ditampilkan
	onSelect: (Long) -> Unit			// fungsi yang dipanggil dengan id terpilih
) {
	var expanded by remember { mutableStateOf(false) }	// menu terbuka atau tertutup

	// Box menumpul tombol dan menu di posisi yang sama
	Box {
		OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
			Text("$label ${selectedText ?: "Pilih"}")
		}
		DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
			options.forEach { (id, text) ->
				DropdownMenuItem(
					text = { Text(text) },
					onClick = {
						onSelect(id)
						expanded = false
					}
				)
			}
		}
	}
}
