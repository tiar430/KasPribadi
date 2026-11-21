plugins {
	id("com.android.application")					// modul ini menghasilkan APK
	id("org.jetbrains.kotlin.android")				// pakai bahasa Kotlin
	id("org.jetbrains.kotlin.plugin.compose")		// compiler untuk Jetpack compose
	id("org.jetbrains.kotlin.plugin.serialization")	// mengubah class route menjadi data yang bisa dikirim antar layar
	id("com.google.devtools.ksp")					// aktifkan KSP di modul app
}

android {
	namespace = "com.tiar.kas"	// package untuk kode dan resource
	compileSdk = 35				// SDK Android 15 untuk compile

	defaultConfig {
		applicationId = "com.tiar.kas"	// ID unik aplikasi di HP
		minSdk = 26						// minimal Android 8
		targetSdk = 35
		versionCode = 1					// naikkan tiap update APK
		versionName = "0.1"
	}

	buildFeatures {
		compose = true	// aktifkan Compose untuk membuat UI
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
}

kotlin {
	compilerOptions {
		jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
	}
}

ksp {
    // lokasi file JSON struktur database, dibuat otomatis saat build
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
	// BOM menyamakan versi semua library Compose
	val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
	implementation(composeBom)
	implementation("androidx.compose.ui:ui")					// inti UI Compose
	implementation("androidx.compose.material3:material3")		// komponen Material 3
	implementation("androidx.activity:activity-compose:1.9.3")	// menghubungkan Activity dengan Compose
	implementation("androidx.core:core-ktx:1.15.0")
	implementation("androidx.room:room-runtime:2.6.1")			// mesin database Room
	implementation("androidx.room:room-ktx:2.6.1")				// dukungan coroutine dan Flow
	ksp("androidx.room:room-compiler:2.6.1")					// generator kode, hanya jalan saat build
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")	// untuk lifecycleScope
	implementation("androidx.navigation:navigation-compose:2.8.5")		// librari navigasi
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")	// runtime serialization
}
