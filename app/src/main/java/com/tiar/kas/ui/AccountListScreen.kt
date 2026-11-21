package com.tiar.kas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.AccountGroup
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.util.formatRupiah
import com.tiar.kas.util.label

@Composable
fun AccountListScreen(
    db: AppDatabase,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val balances by remember { db.accountDao().observeBalances() }.collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // judul di kiri, tombol kembali di kanan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,   // dorong isi ke dua ujung
                verticalAlignment = Alignment.CenterVertically      // sejajarkan tinggi teks dan tombol
            ) {
                Text("Kelola akun", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = onBack) { Text("Kembali") }
            }

            AccountGroup.entries.forEach { group ->
                val inGroup = balances.filter { it.accountGroup == group }
                if (inGroup.isNotEmpty()) {                          // grup tanpa akun tidak ditampilkan
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(group.label(), style = MaterialTheme.typography.titleMedium)

                    inGroup.forEach { acc ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEdit(acc.id) }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(acc.name)
                            Text(formatRupiah(acc.balance), style = MaterialTheme.typography.bodyLarge)
                            if (!acc.includeInTotal) {
                                Text("Tidak dihitung di total", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
