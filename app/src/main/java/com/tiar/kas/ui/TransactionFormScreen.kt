package com.tiar.kas.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.data.Category
import com.tiar.kas.data.CategoryType
import com.tiar.kas.data.Frequency
import com.tiar.kas.data.MoneyTransaction
import com.tiar.kas.data.processRecurring
import com.tiar.kas.data.RecurringRule
import com.tiar.kas.data.TransactionTemplate
import com.tiar.kas.data.TransactionType
import com.tiar.kas.util.formatDateOnly
import com.tiar.kas.util.formatRupiah
import com.tiar.kas.util.formatTime
import com.tiar.kas.util.label
import com.tiar.kas.util.PhotoStore
import java.io.File
import java.util.Calendar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun TransactionFormScreen(
    db: AppDatabase,
    editId: Long?,                      // null untuk transaksi baru, berisi id untuk edit
    payCardId: Long? = null,
    payAmount: Long = 0L,
    onClose: () -> Unit
) {
    val dao = db.transactionDao()
    val templateDao = db.templateDao()
    val accounts by remember { db.accountDao().observeAll() }.collectAsState(initial = emptyList())
    val categories by remember { db.categoryDao().observeAll() }.collectAsState(initial = emptyList())
    val templates by remember { templateDao.observeAll() }.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var type by rememberSaveable { mutableStateOf(if (payCardId != null) TransactionType.TRANSFER else TransactionType.PENGELUARAN) }
    var dateMillis by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var amountText by rememberSaveable { mutableStateOf(if (payAmount > 0) payAmount.toString() else "") }
    var accountId by rememberSaveable { mutableStateOf<Long?>(null) }
    var toAccountId by rememberSaveable { mutableStateOf(payCardId) }
    var categoryId by rememberSaveable { mutableStateOf<Long?>(null) }
    var note by rememberSaveable { mutableStateOf(if (payCardId != null) "Bayar tagihan kartu" else "") }
    var repeat by rememberSaveable { mutableStateOf<Frequency?>(null) }
    var photoName by rememberSaveable { mutableStateOf<String?>(null) }
    var originalPhoto by rememberSaveable { mutableStateOf<String?>(null) }
    var cameraTemp by rememberSaveable { mutableStateOf<String?>(null) }
    var showPhoto by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    // true kalau data sudah dimuat, transaksi baru dianggap sudah dimuat sejak awal
    var loaded by rememberSaveable { mutableStateOf(editId == null) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    var showTemplateDialog by rememberSaveable { mutableStateOf(false) }
    var templateName by rememberSaveable { mutableStateOf("") }
    var templateError by rememberSaveable { mutableStateOf<String?>(null) }

    // berjalan sekali saat form muncul, dan berjalan lagi hanya kalau editId berubah
    LaunchedEffect(editId) {
        if (!loaded && editId != null) {
            val tx = dao.getById(editId)
            if (tx != null) {
                type = tx.type
                dateMillis = tx.date
                amountText = tx.amount.toString()
                accountId = tx.accountId
                toAccountId = tx.toAccountId
                categoryId = tx.categoryId
                note = tx.note
                photoName = tx.photoPath
                originalPhoto = tx.photoPath
            }
            loaded = true      // cegah pemuatan ulang yang menimpa editanmu saat HP dilipat
        }
    }

    val categoryType =
        if (type == TransactionType.PEMASUKAN) CategoryType.PEMASUKAN
        else CategoryType.PENGELUARAN
    val categoryOptions = buildCategoryOptions(categories, categoryType)

    // hasil pilihan foto datang lewat lambda ini, uri null kalau batal
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
    	if (uri != null) {
    		scope.launch {
    			val imported = PhotoStore.import(context, uri)
    			if (imported != null) photoName = imported
    			else Toast.makeText(context, "Foto gagal dibaca", Toast.LENGTH_SHORT).show()
    		}
    	}
    }

    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
    	val temp = cameraTemp
    	if (success && temp != null) {
    		scope.launch {
    			val file = File(context.cacheDir, "camera/$temp")
    			val imported = PhotoStore.import(context, Uri.fromFile(file))
    			file.delete()
    			if (imported != null) photoName = imported
    			else Toast.makeText(context, "Foto gagal dibaca", Toast.LENGTH_SHORT).show()
    		}
    	}
    	cameraTemp = null
    }

    val amountColor = when (type) {
    	TransactionType.PEMASUKAN -> MaterialTheme.colorScheme.primary
    	TransactionType.PENGELUARAN -> MaterialTheme.colorScheme.error
    	TransactionType.TRANSFER -> MaterialTheme.colorScheme.onSurface
    }

    fun openDatePicker() {
    	val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
    	DatePickerDialog(
    		context,
    		{ _, year, month, day ->
    			cal.set(year, month, day)
    			dateMillis = cal.timeInMillis
    		},
    		cal.get(Calendar.YEAR),
    		cal.get(Calendar.MONTH),
    		cal.get(Calendar.DAY_OF_MONTH)
    	).show()
    }

    fun openTimePicker() {
    	val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
    	val listener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
    		cal.set(Calendar.HOUR_OF_DAY, hour)
    		cal.set(Calendar.MINUTE, minute)
    		cal.set(Calendar.SECOND, 0)
    		cal.set(Calendar.MILLISECOND, 0)
    		dateMillis = cal.timeInMillis
    	}
    	TimePickerDialog(
    		context,
    		listener,
    		cal.get(Calendar.HOUR_OF_DAY),
    		cal.get(Calendar.MINUTE),
    		true
    	).show()
    }

    fun save() {
		val amount = amountText.toLongOrNull() ?: 0L
		val from = accountId
		val to = toAccountId
		val cat = categoryId

		error = when {
			amount <= 0 -> "Nominal harus lebih dari 0"
			from == null -> "Pilih akun"
			type == TransactionType.TRANSFER && to == null -> "Pilih akun tujuan"
			type == TransactionType.TRANSFER && to == from -> "Akun asal dan tujuan tidak boleh sama"
			type != TransactionType.TRANSFER && cat == null -> "Pilih kategori"
			else -> null
		}

		if (error == null && from != null) {
			val freq = repeat
			scope.launch {
				if (editId == null && freq != null) {
					db.recurringDao().insert(
						RecurringRule(
							type = type,
                    		amount = amount,
                    		accountId = from,
                    		toAccountId = if (type == TransactionType.TRANSFER) to else null,
                    		categoryId = if (type == TransactionType.TRANSFER) null else cat,
                    		note = note.trim(),
                    		frequency = freq,
                    		startDate = dateMillis
                    	)
                    )
                    val created = processRecurring(db)
                    Toast.makeText(context, "Aturan berulang dibuat, $created transaksi dicatat", Toast.LENGTH_SHORT).show()
				} else {
                    val tx = MoneyTransaction(
                    	id = editId ?: 0,
                    	date = dateMillis,
                    	type = type,
                    	amount = amount,
                    	accountId = from,
                    	toAccountId = if (type == TransactionType.TRANSFER) to else null,
                    	categoryId = if (type == TransactionType.TRANSFER) null else cat,
                    	note = note.trim(),
                    	photoPath = photoName
                    )
                    if (editId == null) dao.insert(tx) else dao.update(tx)
                    if (originalPhoto != null && originalPhoto != photoName) PhotoStore.delete(context, originalPhoto)
				}
				onClose()
			}
		}
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 16.dp, vertical = 8.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		ScreenHeader(
			title = when {
				editId != null -> "Edit Transaksi"
				payCardId != null -> "Bayar Tagihan"
				else -> "Transaksi Baru"
			},
			onBack = onClose
		)

		if (editId == null && payCardId == null && templates.isNotEmpty()) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.horizontalScroll(rememberScrollState()),
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				templates.forEach { t ->
					AssistChip(
						onClick = {
							type = t.type
							amountText = if (t.amount > 0) t.amount.toString() else ""
							accountId = t.accountId
							toAccountId = t.toAccountId
							categoryId = t.categoryId
							note = t.note
							error = null
						},
						label = { Text(t.name) }
					)
				}
			}
		}

		// Tiga tombol menyatu, hanya satu yang aktif
		val types = TransactionType.entries
		SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
			types.forEachIndexed { index, t ->
				SegmentedButton(
					selected = type == t,
					onClick = {
						if (type != t) categoryId = null
						type = t
					},
					// sudut bulat hanya di ujung kiri tombol pertama, dan ujung kanan tombol terakhir
					shape = SegmentedButtonDefaults.itemShape(index = index, count = types.size),
					label = { Text(t.name.lowercase().replaceFirstChar { it.uppercase() }) }
				)
			}
		}

		AmountInput(value = amountText, onValueChange = { amountText = it }, color = amountColor)

		FormCard {
			Row(modifier = Modifier.fillMaxWidth()) {
				FormClickRow(
					icon = Icons.Outlined.CalendarToday,
					label = "Tanggal",
					value = formatDateOnly(dateMillis),
					onClick = { openDatePicker() },
					modifier = Modifier.weight(1f)
				)
			}
			FormDivider()
			PickerField(
				label = if (type == TransactionType.TRANSFER) "Dari Akun" else "Akun",
				selectedText = accounts.firstOrNull { it.id == accountId }?.name,
				options = accounts.map { it.id to it.name },
				onSelect = { accountId = it },
				icon = Icons.Outlined.AccountBalanceWallet,
				bordered = false
			)
			FormDivider()
			if (type == TransactionType.TRANSFER) {
				PickerField(
					label = "Ke Akun",
					selectedText = accounts.firstOrNull { it.id == toAccountId }?.name,
					options = accounts.filter { it.id != accountId }.map { it.id to it.name },
					onSelect = { toAccountId = it },
					icon = Icons.Outlined.SwapHoriz,
					bordered = false
				)
			} else {
				PickerField(
					label = "Kategori",
					selectedText = categoryOptions.firstOrNull { it.first == categoryId }?.second,
					options = categoryOptions,
					onSelect = { categoryId = it },
					icon = Icons.Outlined.Category,
					bordered = false
				)
			}
			if (editId == null) {
				FormDivider()
				PickerField(
					label = "Ulangi",
					selectedText = repeat?.label() ?: "Tidak berulang",
					options = listOf(-1L to "Tidak berulang") + Frequency.entries.map { it.ordinal.toLong() to it.label() },
					onSelect = { id -> repeat = if (id == -1L) null else Frequency.entries[id.toInt()] },
					icon = Icons.Outlined.Repeat,
					bordered = false
				)
				repeat?.let { freq ->
					Text(
						"Mulai ${formatDateOnly(dateMillis)}, lalu ${freq.label().lowercase()}",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						modifier = Modifier.padding(start = 48.dp, end = 12.dp, bottom = 8.dp)
					)
				}
			}
			FormDivider()
			// Kolom isian tanpa kotak dan tanpa garis, menyatu dengan kartu
			TextField(
				value = note,
				onValueChange = { note = it },
				placeholder = { Text("Catatan") },
				leadingIcon = { Icon(Icons.Outlined.EditNote, contentDescription = null) },
				colors = TextFieldDefaults.colors(
					focusedContainerColor = Color.Transparent,
					unfocusedContainerColor = Color.Transparent,
					focusedIndicatorColor = Color.Transparent,
					unfocusedIndicatorColor = Color.Transparent
				),
				modifier = Modifier.fillMaxWidth()
			)
		}

		FormCard {
			val name = photoName
			if (name != null) {
				Row(
					modifier = Modifier.padding(12.dp),
					horizontalArrangement = Arrangement.spacedBy(12.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					PhotoThumbnail(
						name = name,
						modifier = Modifier
							.size(64.dp)
							.clip(MaterialTheme.shapes.small)
							.clickable { showPhoto = true }
					)
					Column(modifier = Modifier.weight(1f)) {
						Text("Foto Struk", style = MaterialTheme.typography.titleSmall)
						Text("Ketuk foto untuk zoom gambar", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
					}
					IconButton(onClick = { photoName = null }) {
						Icon(Icons.Outlined.Delete, contentDescription = "Hapus Foto", tint = MaterialTheme.colorScheme.error)
					}
				}
				FormDivider()
			}
			Row(modifier = Modifier.fillMaxWidth()) {
				TextButton(
					onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
					modifier = Modifier.weight(1f)
				) {
					Icon(Icons.Outlined.PhotoLibrary, contentDescription = null)
					Spacer(modifier = Modifier.width(8.dp))
					Text("Galeri")
				}
				TextButton(
					onClick = {
						val (tempName, uri) = PhotoStore.newCameraTarget(context)
						cameraTemp = tempName
						camera.launch(uri)
					},
					modifier = Modifier.weight(1f)
				) {
					Icon(Icons.Outlined.PhotoCamera, contentDescription = null)
					Spacer(modifier = Modifier.width(8.dp))
					Text("Kamera")
				}
			}
		}

		error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }

		Button(
			onClick = { save() },
			shape = MaterialTheme.shapes.large,
			modifier = Modifier.fillMaxWidth().height(52.dp)
		) {
			Text("Simpan", style = MaterialTheme.typography.titleSmall)
		}

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			TextButton(onClick = {
				templateError = null
				showTemplateDialog = true
			}) {
				Icon(Icons.Outlined.BookmarkAdd, contentDescription = null)
				Spacer(modifier = Modifier.width(6.dp))
				Text("Simpan sebagai template")
			}
			if (editId != null) {
				TextButton(onClick = { showDeleteDialog = true }) {
					Text("Hapus", color = MaterialTheme.colorScheme.error)
				}
			}
		}
	}

    // dialog konfirmasi supaya transaksi tidak terhapus karena salah ketuk
    if (showDeleteDialog && editId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },   // ketuk di luar dialog menutupnya
            title = { Text("Hapus transaksi?") },
            text = { Text("Transaksi ini dihapus permanen dan saldo akun dihitung ulang.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        dao.deleteById(editId)
                        PhotoStore.delete(context, originalPhoto)
                        onClose()
                    }
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    if (showTemplateDialog) {
    	AlertDialog(
    		onDismissRequest = { showTemplateDialog = false },
    		title = { Text("Simpan sebagai template") },
    		text = {
    			Column {
    				OutlinedTextField(
    					value = templateName,
    					onValueChange = { templateName = it },
    					label = { Text("Nama template") },
    					singleLine = true
    				)
    				Text("Nominal kosong berarti diisi saat template dipakai", style = MaterialTheme.typography.bodySmall)
    				templateError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    			}
    		},
    		confirmButton = {
    			TextButton(onClick = {
    				val name = templateName.trim()
    				val from = accountId
    				templateError = when {
    					name.isEmpty() -> "Nama template wajib diisi"
    					from == null -> "Pilih akun di form dulu"
    					type == TransactionType.TRANSFER && toAccountId == null -> "Pilih akun tujuan di form dulu"
    					type != TransactionType.TRANSFER && categoryId == null -> "Pilih kategori di form dulu"
    					else -> null
    				}
    				if (templateError == null && from != null) {
    					scope.launch {
    						templateDao.insert(
    							TransactionTemplate(
    								name = name,
    								type = type,
    								amount = amountText.toLongOrNull() ?: 0L,
    								accountId = from,
    								toAccountId = if (type == TransactionType.TRANSFER) toAccountId else null,
    								categoryId = if (type == TransactionType.TRANSFER) null else categoryId,
    								note = note.trim(),
    								sortOrder = templateDao.nextSortOrder()
    							)
    						)
    						showTemplateDialog = false
    						templateName = ""
    						Toast.makeText(context, "Template tersimpan", Toast.LENGTH_SHORT).show()
    					}
    				}
    			}) { Text("Simpan") }
    		},
    		dismissButton = {
    			TextButton(onClick = { showTemplateDialog = false }) { Text("Batal") }
    		}
    	)
    }

    val viewName = photoName
    if (showPhoto && viewName != null) {
    	Dialog(onDismissRequest = { showPhoto = false }) {
    		PhotoThumbnail(
    			name = viewName,
    			maxSide = 1600,
    			contentScale = ContentScale.Fit,
    			modifier = Modifier
    				.fillMaxWidth()
    				.clickable { showPhoto = false }
    		)
    	}
    }
}

fun buildCategoryOptions(all: List<Category>, type: CategoryType): List<Pair<Long, String>> {
    val result = mutableListOf<Pair<Long, String>>()
    all.filter { it.type == type && it.parentId == null }.forEach { main ->
        result += main.id to main.name
        all.filter { it.parentId == main.id }.forEach { sub ->
            result += sub.id to "${main.name} / ${sub.name}"
        }
    }
    return result
}
