package com.tiar.kas.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LockScreen(
	onCheck: (String) -> Boolean,	// true kalau PIN benar
	onUnlocked: () -> Unit
) {
	var pin by rememberSaveable { mutableStateOf("") }
	var error by rememberSaveable { mutableStateOf<String?>(null) }

	// tombol kembali tidak boleh membuka layar dibawah kunci
	BackHandler { }

	// surface layar penuh menutupi dan menahan sentuhan ke layar di bawahnya
	Surface(modifier = Modifier.fillMaxSize()) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(32.dp),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text("Kas Pribadi terkunci", style = MaterialTheme.typography.titleLarge)
			Spacer(modifier = Modifier.height(16.dp))
			OutlinedTextField(
				value = pin,
				onValueChange = { input ->
					pin = input.filter { it.isDigit() }.take(6)
					error = null
				},
				label = { Text("PIN") },
				singleLine = true,
				visualTransformation = PasswordVisualTransformation(),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
			)
			error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
			Spacer(modifier = Modifier.height(16.dp))
			Button(onClick = {
				if (onCheck(pin)) {
					pin = ""
					onUnlocked()
				} else {
					error = "PIN salah"
					pin = ""
				}
			}) {
				Text("Buka")
			}
		}
	}
}
