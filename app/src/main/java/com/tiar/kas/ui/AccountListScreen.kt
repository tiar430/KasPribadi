package com.tiar.kas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.MoneyOff
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.tiar.kas.data.AccountBalance
import com.tiar.kas.data.AccountGroup
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.ui.theme.Tabular
import com.tiar.kas.util.formatRupiah
import com.tiar.kas.util.label
import kotlin.math.abs

// ikon per grup, hanya dipakai di file ini
private fun AccountGroup.icon(): ImageVector = when (this) {
	AccountGroup.TUNAI -> Icons.Outlined.Payments
	AccountGroup.BANK -> Icons.Outlined.AccountBalance
	AccountGroup.E_WALLET -> Icons.Outlined.AccountBalanceWallet
	AccountGroup.KARTU_KREDIT -> Icons.Outlined.CreditCard
	AccountGroup.INVESTASI -> Icons.Outlined.Savings
	AccountGroup.PINJAMAN -> Icons.Outlined.MoneyOff
}

@Composable
fun AccountListScreen(
	db: AppDatabase,
	onBack: () -> Unit,
	onAdd: () -> Unit,
	onEdit: (Long) -> Unit,
	onPayCard: (Long, Long) -> Unit
) {
	val balances by remember { db.accountDao().observeBalances() }.collectAsState(initial = emptyList())

	// hanya akun yang dihitung di total
	val counted = balances.filter { it.includeInTotal }
	val assets = counted.filter { it.balance > 0 }.sumOf { it.balance }
	val debts = abs(counted.filter { it.balance < 0 }.sumOf { it.balance })	// Jumlah minus dijadikan positif

	Scaffold(
		containerColor = MaterialTheme.colorScheme.background,
		floatingActionButton = {
			FloatingActionButton(
				onClick = onAdd,
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = MaterialTheme.colorScheme.onPrimary,
				shape = MaterialTheme.shapes.large
			) {
				Icon(Icons.Outlined.Add, contentDescription = "Tambah Akun")
			}
		}
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.verticalScroll(rememberScrollState())
				.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			ScreenHeader("Akun")
			NetWorthCard(assets = assets, debts = debts)

			AccountGroup.entries.forEach { group ->
				val inGroup = balances.filter { it.accountGroup == group }
				if (inGroup.isNotEmpty()) {
					Column {
						Text(
							group.label(),
							style = MaterialTheme.typography.labelLarge,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
							modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
						)
						Surface(
							color = MaterialTheme.colorScheme.surfaceContainerLowest,
							shape = MaterialTheme.shapes.medium,
							border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
							modifier = Modifier.fillMaxWidth()
						) {
							Column {
								inGroup.forEachIndexed { index, acc ->
									if (index > 0) {
										HorizontalDivider(modifier = Modifier.padding(start = 68.dp), color = MaterialTheme.colorScheme.outlineVariant)
									}
									AccountRow(
										db = db,
										acc = acc,
										onClick = { onEdit(acc.id) },
										onPay = { amount -> onPayCard(acc.id, amount) }
									)
								}
							}
						}
					}
				}
			}
		}
	}
}

// kartu hijau muda berisi aset, utang, dan bersih
@Composable
private fun NetWorthCard(assets: Long, debts: Long) {
	Surface(
		color = MaterialTheme.colorScheme.primaryContainer,
		contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
		shape = MaterialTheme.shapes.large,
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(20.dp)) {
			Text("Bersih", style = MaterialTheme.typography.labelLarge)
			Text(formatRupiah(assets - debts), style = MaterialTheme.typography.headlineMedium.merge(Tabular))
			Spacer(modifier = Modifier.height(12.dp))
			Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
				Column {
					Text("Aset", style = MaterialTheme.typography.labelMedium)
					Text(formatRupiah(assets), style = MaterialTheme.typography.titleSmall.merge(Tabular))
				}
				Column {
					Text("Utang", style = MaterialTheme.typography.labelMedium)
					Text(
						formatRupiah(debts),
						style = MaterialTheme.typography.titleSmall.merge(Tabular),
						color = if (debts > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
					)
				}
			}
		}
	}
}

@Composable
private fun AccountRow(
	db: AppDatabase,
	acc: AccountBalance,
	onClick: () -> Unit,
	onPay: (Long) -> Unit
) {
	Column(
		modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 12.dp)
	) {
		Row(verticalAlignment = Alignment.CenterVertically) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.size(40.dp)
					.background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
				contentAlignment = Alignment.Center
			) {
				Icon(
					acc.accountGroup.icon(),
					contentDescription = null,
					modifier = Modifier.size(20.dp),
					tint = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
			Spacer(modifier = Modifier.width(12.dp))
			Column(modifier = Modifier.weight(1f)) {
				Text(acc.name, style = MaterialTheme.typography.bodyLarge)
				if (!acc.includeInTotal) {
					Text("Tidak dihitung di total", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
				}
			}
			Text(
				formatRupiah(acc.balance),
				style = MaterialTheme.typography.titleSmall.merge(Tabular),
				color = if (acc.balance < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
			)
		}

		// info tagihan menjorok sejajar nama akun
		val s = acc.statementDay
		val d = acc.dueDay
		if (acc.accountGroup == AccountGroup.KARTU_KREDIT && s != null && d != null) {
			Box(modifier = Modifier.padding(start = 52.dp)) {
				CardBillInfo(
					db = db,
					cardId = acc.id,
					statementDay = s,
					dueDay = d,
					onPay = onPay
				)
			}
		}
	}
}
