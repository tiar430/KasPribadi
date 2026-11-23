package com.tiar.kas

import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.data.processRecurring
import com.tiar.kas.data.seedIfEmpty
import com.tiar.kas.ui.AppNavigation
import com.tiar.kas.ui.CrashReportDialog
import com.tiar.kas.ui.LockScreen
import com.tiar.kas.util.PhotoStore
import com.tiar.kas.util.PinStore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase
    private lateinit var pinStore: PinStore

    // state Compose milik Activity, layar ikut berubah saat nilainya berubah
    private var locked by mutableStateOf(false)
    private var backgroundAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.get(this)
        pinStore = PinStore(this)

        // saat Fold 5 dilipat, Activity dibuat ulang, jadi status terbuka diambil dari savedInstanceState
        val wasUnlocked = savedInstanceState?.getBoolean(KEY_UNLOCKED) ?: false
        locked = pinStore.hasPin() && !wasUnlocked

        lifecycleScope.launch {
        	seedIfEmpty(db)
        	if (savedInstanceState == null) {
        		PhotoStore.cleanup(this@MainActivity, db.transactionDao().allPhotoNames().toSet())
        	}
        }

        setContent {
            MaterialTheme {
                Box {
                    AppNavigation(db = db)
                    CrashReportDialog()
                    if (locked) {
                        // digambar paling akhir, jadi berada paling atas
                        LockScreen(
                            onCheck = { pin -> pinStore.check(pin) },
                            onUnlocked = { locked = false }
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val away = SystemClock.elapsedRealtime() - backgroundAt
        if (backgroundAt > 0 && away > LOCK_AFTER_MS && pinStore.hasPin()) locked = true
        backgroundAt = 0L
    }

    override fun onStop() {
        super.onStop()
        // berhenti karena lipat layar tidak dihitung sebagai keluar aplikasi
        if (!isChangingConfigurations) backgroundAt = SystemClock.elapsedRealtime()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_UNLOCKED, !locked)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val created = processRecurring(db)
            if (created > 0) {
                Toast.makeText(this@MainActivity, "$created transaksi berulang dicatat", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val KEY_UNLOCKED = "unlocked"
        private const val LOCK_AFTER_MS = 30_000L      // 30 detik
    }
}
