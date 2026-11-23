package com.tiar.kas.util

import android.content.Context
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PinStore(context: Context) {
	// file penyimpanan kecil milik aplikasi, terpisah dari database
	private val prefs = context.getSharedPreferences("security", Context.MODE_PRIVATE)
	fun hasPin(): Boolean = prefs.contains(KEY_HASH)
	fun setPin(pin: String) {
		val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
		prefs.edit()
			.putString(KEY_SALT, encode(salt))
			.putString(KEY_HASH, encode(hash(pin, salt)))
			.apply()
	}

	fun check(pin: String): Boolean {
		val salt = prefs.getString(KEY_SALT, null) ?: return false
		val saved = prefs.getString(KEY_HASH, null) ?: return false
		return encode(hash(pin, decode(salt))) == saved
	}

	fun clear() {
		prefs.edit().remove(KEY_HASH).remove(KEY_SALT).apply()
	}

	private fun hash(pin: String, salt: ByteArray): ByteArray {
		val spec = PBEKeySpec(pin.toCharArray(), salt, ITERATIONS, 256)
		return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
	}

	private fun encode(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)
	private fun decode(text: String): ByteArray = Base64.getDecoder().decode(text)

	companion object {
		private const val KEY_HASH = "pin_hash"
		private const val KEY_SALT = "pin_salt"
		private const val ITERATIONS = 20_000
	}
}
