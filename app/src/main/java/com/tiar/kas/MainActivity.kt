package com.tiar.kas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.lifecycleScope
import com.tiar.kas.data.AppDatabase
import com.tiar.kas.data.seedIfEmpty
import com.tiar.kas.ui.AppNavigation
import com.tiar.kas.ui.CrashReportDialog
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.get(this)
        lifecycleScope.launch { seedIfEmpty(db) }

        setContent {
            MaterialTheme {
            	AppNavigation(db = db)	// semua urusan layar diserahkan ke Navigation
            	CrashReportDialog()
            }
        }
    }
}
