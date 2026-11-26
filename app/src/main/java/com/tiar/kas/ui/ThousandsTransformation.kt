package com.tiar.kas.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class ThousandsTransformation(private val prefix: String = "") : VisualTransformation {
	override fun filter(text: AnnotatedString): TransformedText {
		val digits = text.text
		val n = digits.length
		if (n == 0) return TransformedText(AnnotatedString(""), OffsetMapping.Identity)

		val core = buildString {
			digits.forEachIndexed { i, c ->
				if (i > 0 && (n - i) % 3 == 0) append('.')
				append(c)
			}
		}
		val p = prefix.length
		val mapping = object : OffsetMapping {
			override fun originalToTransformed(offset: Int): Int =
				p + offset + (1 until offset).count { (n - it) % 3 == 0 }

			override fun transformedToOriginal(offset: Int): Int {
				val inCore = (offset - p).coerceIn(0, core.length)
				return (inCore - core.take(inCore).count { it == '.' }).coerceIn(0, n)
			}
		}
		return TransformedText(AnnotatedString(prefix + core), mapping)
	}
}
