package com.tiar.kas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.tiar.kas.util.PinStore

@Composable
fun SecurityScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val store = remember { PinStore(context) }

    var hasPin by remember { mutableStateOf(store.hasPin()) }
    var oldPin by rememberSaveable { mutableStateOf("") }
    var newPin by rememberSaveable { mutableStateOf("") }
    var confirmPin by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    var isError by rememberSaveable { mutableStateOf(false) }

    fun resetFields() {
        oldPin = ""
        newPin = ""
        confirmPin = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScreenHeader("Keamanan", onBack = onBack)
            TextButton(onClick = onBack) { Text("Kembali") }
        }

        Text(
            if (hasPin) "PIN aktif. Aplikasi terkunci saat dibuka dan setelah 30 detik di latar belakang"
            else "PIN belum diatur. PIN yang lupa tidak bisa dipulihkan",
            style = MaterialTheme.typography.bodySmall
        )

        if (hasPin) PinField("PIN lama", oldPin) { oldPin = it }
        PinField(if (hasPin) "PIN baru" else "PIN", newPin) { newPin = it }
        PinField("Ulangi PIN", confirmPin) { confirmPin = it }

        message?.let {
            Text(it, color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        }

        Button(onClick = {
            val err = when {
                hasPin && !store.check(oldPin) -> "PIN lama salah"
                newPin.length !in 4..6 -> "PIN harus 4 sampai 6 angka"
                newPin != confirmPin -> "Ulangi PIN tidak sama"
                else -> null
            }
            if (err != null) {
                message = err
                isError = true
            } else {
                store.setPin(newPin)
                hasPin = true
                resetFields()
                message = "PIN tersimpan"
                isError = false
            }
        }) {
            Text(if (hasPin) "Ubah PIN" else "Simpan PIN")
        }

        if (hasPin) {
            TextButton(onClick = {
                if (store.check(oldPin)) {
                    store.clear()
                    hasPin = false
                    resetFields()
                    message = "PIN dihapus"
                    isError = false
                } else {
                    message = "Isi PIN lama yang benar untuk menghapus PIN"
                    isError = true
                }
            }) {
                Text("Hapus PIN", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// isian PIN yang dipakai tiga kali di layar ini
@Composable
private fun PinField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { input -> onChange(input.filter { it.isDigit() }.take(6)) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.fillMaxWidth()
    )
}
