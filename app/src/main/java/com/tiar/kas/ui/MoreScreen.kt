package com.tiar.kas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun MoreScreen(
	onBudget: () -> Unit,
	onTemplates: () -> Unit,
	onRecurring: () -> Unit,
	onSecurity: () -> Unit,
	onBackup: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(vertical = 16.dp)
	) {
		ScreenHeader(
			"Lainnya",
			modifier = Modifier.padding(horizontal = 16.dp)
		)
		MoreItem(Icons.Outlined.Savings, "Budget", "Batas pengeluaran per kategori", onBudget)
		MoreItem(Icons.Outlined.Bookmarks, "Template", "Transaksi rutin yang sering digunakan", onTemplates)
		MoreItem(Icons.Outlined.Repeat, "Transaksi berulang", "Gaji, cicilan, dan langganan", onRecurring)
		MoreItem(Icons.Outlined.Lock, "Keamanan", "PIN pembuka aplikasi", onSecurity)
		MoreItem(Icons.Outlined.Backup, "Backup dan ekspor", "Simpan, pulihkan, format CSV", onBackup)
	}
}

// satu baris menu dengan ikon lingkaran hijau muda
@Composable
private fun MoreItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
	ListItem(
		headlineContent = { Text(title) },
		supportingContent = { Text(subtitle) },
		leadingContent = {
			Box(
				modifier = Modifier
					.size(40.dp)
					.background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
				contentAlignment = Alignment.Center
			) {
				// contentDescription null karena judul di sebelahnya sudah menjelaskan
				Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
			}
		},
		trailingContent = {
			Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null)
		},
		colors = ListItemDefaults.colors(containerColor = Color.Transparent),
		modifier = Modifier.clickable(onClick = onClick)
	)
}
