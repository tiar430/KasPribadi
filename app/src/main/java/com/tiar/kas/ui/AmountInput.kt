package com.tiar.kas.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.tiar.kas.ui.theme.Tabular

// input nominal besar di tengah, warna mengikuti jenis transaksi
@Composable
fun AmountInput(
    value: String,
    onValueChange: (String) -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    val style = MaterialTheme.typography.displaySmall
        .merge(Tabular)
        .copy(color = color, textAlign = TextAlign.Center)
    val transformation = remember { ThousandsTransformation(prefix = "Rp ") }   // dibuat sekali

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Nominal", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        // BasicTextField adalah kolom isian tanpa garis dan tanpa kotak, tampilannya kita atur sendiri
        BasicTextField(
            value = value,
            onValueChange = { input -> onValueChange(input.filter { it.isDigit() }.take(13)) },
            textStyle = style,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = transformation,
            cursorBrush = SolidColor(color),              // warna kursor sama dengan angka
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (value.isEmpty()) {
                        Text("Rp 0", style = style.copy(color = MaterialTheme.colorScheme.outline))   // placeholder abu abu
                    }
                    innerTextField()                      // kolom isian asli digambar di sini
                }
            }
        )
    }
}
