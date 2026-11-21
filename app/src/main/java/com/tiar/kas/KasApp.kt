package com.tiar.kas

import android.app.Application
import java.io.File

class KasApp : Application() {
	override fun onCreate() {
		super.onCreate()

		// simpan penanganan error bawaan Android supaya tetap dijalankan setelah kita
		val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

		Thread.setDefaultUncaughtExceptionHandler { thread, error ->
			// tulis stack trace ke file di penyimpanan internal aplikasi
			File(filesDir, "last_crash.txt").writeText(error.stackTraceToString())
			defaultHandler?.uncaughtException(thread, error)	// lanjutkan force close seperti biasa
		}
	}
}
