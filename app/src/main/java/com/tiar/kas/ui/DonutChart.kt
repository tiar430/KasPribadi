package com.tiar.kas.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun DonutChart(
    values: List<Long>,
    colors: List<Color>,            // satu warna untuk setiap nilai, urutannya sama
    modifier: Modifier = Modifier
) {
    val total = values.sum().toFloat()

    Canvas(modifier = modifier) {
        if (total <= 0f) return@Canvas                       // tidak ada data, tidak menggambar apa pun

        // size adalah ukuran area Canvas dalam piksel
        val strokeWidth = size.minDimension * 0.18f          // tebal cincin 18 persen dari sisi terpendek
        val diameter = size.minDimension - strokeWidth       // dikurangi supaya cincin tidak terpotong di tepi
        val topLeft = Offset(
            (size.width - diameter) / 2f,                    // posisikan lingkaran di tengah
            (size.height - diameter) / 2f
        )

        var startAngle = -90f                                // mulai dari arah jam 12
        values.forEachIndexed { index, value ->
            val sweep = value / total * 360f                 // besar sudut sesuai porsi nilai
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,                           // false menggambar busur, bukan potongan pizza
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth)          // garis tebal, bagian tengah tetap kosong
            )
            startAngle += sweep                              // potongan berikutnya mulai di ujung potongan ini
        }
    }
}
