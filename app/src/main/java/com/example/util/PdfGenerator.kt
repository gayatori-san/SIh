package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.example.data.Report
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {
    fun generatePdf(context: Context, uri: Uri, report: Report) {
        val pdfDocument = PdfDocument()
        drawReportPage(pdfDocument, report)
        writeAndCloseDocument(context, uri, pdfDocument)
    }

    fun generateConsolidatedPdf(context: Context, uri: Uri, reports: List<Report>) {
        val pdfDocument = PdfDocument()
        reports.forEach { report ->
            drawReportPage(pdfDocument, report)
        }
        writeAndCloseDocument(context, uri, pdfDocument)
    }

    private fun writeAndCloseDocument(context: Context, uri: Uri, pdfDocument: PdfDocument) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }

    private fun drawReportPage(pdfDocument: PdfDocument, report: Report) {
        val pageInfo = PdfDocument.PageInfo.Builder(595, 875, pdfDocument.pages.size + 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        
        val primaryColor = Color.parseColor("#1E3A8A") // Deep Blue
        val accentColor = Color.parseColor("#3B82F6") // Blue Accent
        val successColor = Color.parseColor("#10B981") // Green
        val errorColor = Color.parseColor("#EF4444") // Red
        val surfaceColor = Color.parseColor("#F3F4F6") // Light Gray
        val borderColor = Color.parseColor("#E5E7EB") // Gray Border
        val textColor = Color.parseColor("#1F2937") // Dark Gray

        // Paints
        val headerBgPaint = Paint().apply { color = primaryColor; style = Paint.Style.FILL }
        val titlePaint = Paint().apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); textSize = 28f; color = Color.WHITE; textAlign = Paint.Align.LEFT }
        val subtitlePaint = Paint().apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL); textSize = 14f; color = Color.parseColor("#BFDBFE"); textAlign = Paint.Align.LEFT }
        
        val sectionTitlePaint = Paint().apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); textSize = 14f; color = primaryColor }
        val labelPaint = Paint().apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); textSize = 10f; color = Color.GRAY }
        val valuePaint = Paint().apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL); textSize = 12f; color = textColor }
        
        val passPaint = Paint().apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); textSize = 12f; color = successColor; textAlign = Paint.Align.RIGHT }
        val failPaint = Paint().apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); textSize = 12f; color = errorColor; textAlign = Paint.Align.RIGHT }
        val tableHeaderPaint = Paint().apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); textSize = 10f; color = Color.WHITE }
        
        val borderPaint = Paint().apply { color = borderColor; style = Paint.Style.STROKE; strokeWidth = 1f }
        val fillPaint = Paint().apply { color = surfaceColor; style = Paint.Style.FILL }

        val startX = 40f
        val pageWidth = 595f
        var currentY = 0f

        // Header Background
        canvas.drawRect(0f, 0f, pageWidth, 120f, headerBgPaint)
        
        // Header Text
        currentY = 50f
        canvas.drawText("MetroCert", startX, currentY, titlePaint)
        currentY += 25f
        canvas.drawText("OIML R-76 Official Verification Report", startX, currentY, subtitlePaint)
        
        // Report Meta
        val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(report.date))
        val metaPaint = Paint(subtitlePaint).apply { textAlign = Paint.Align.RIGHT; color = Color.WHITE }
        canvas.drawText("Cert No: ${report.certificateNo}", pageWidth - startX, 50f, metaPaint)
        canvas.drawText("Date: $dateStr", pageWidth - startX, 75f, metaPaint)

        currentY = 145f

        // Instrument Details Box
        canvas.drawText("INSTRUMENT DETAILS", startX, currentY, sectionTitlePaint)
        currentY += 10f
        
        val rectDetails = RectF(startX, currentY, pageWidth - startX, currentY + 50f)
        canvas.drawRoundRect(rectDetails, 8f, 8f, fillPaint)
        canvas.drawRoundRect(rectDetails, 8f, 8f, borderPaint)
        
        currentY += 18f
        canvas.drawText("MANUFACTURER", startX + 15f, currentY, labelPaint)
        canvas.drawText(report.manufacturer.takeIf { it.isNotBlank() } ?: "N/A", startX + 15f, currentY + 18f, valuePaint)
        
        canvas.drawText("MODEL", startX + 160f, currentY, labelPaint)
        canvas.drawText(report.modelNumber.takeIf { it.isNotBlank() } ?: "N/A", startX + 160f, currentY + 18f, valuePaint)
        
        canvas.drawText("SERIAL NUMBER", startX + 310f, currentY, labelPaint)
        canvas.drawText(report.serialNumber.takeIf { it.isNotBlank() } ?: "N/A", startX + 310f, currentY + 18f, valuePaint)
        
        currentY += 50f
        
        // Metrology Specs Box
        canvas.drawText("METROLOGY SPECIFICATIONS", startX, currentY, sectionTitlePaint)
        currentY += 10f
        
        val rectSpecs = RectF(startX, currentY, pageWidth - startX, currentY + 50f)
        canvas.drawRoundRect(rectSpecs, 8f, 8f, fillPaint)
        canvas.drawRoundRect(rectSpecs, 8f, 8f, borderPaint)
        
        currentY += 18f
        canvas.drawText("ACCURACY CLASS", startX + 15f, currentY, labelPaint)
        canvas.drawText(report.accuracyClass, startX + 15f, currentY + 18f, valuePaint)
        
        canvas.drawText("MAX CAPACITY (kg)", startX + 160f, currentY, labelPaint)
        canvas.drawText(report.maxCapacity.toString(), startX + 160f, currentY + 18f, valuePaint)
        
        canvas.drawText("VERIFICATION INT. e (kg)", startX + 310f, currentY, labelPaint)
        canvas.drawText(report.e.toString(), startX + 310f, currentY + 18f, valuePaint)
        
        currentY += 50f
        
        // Environmental Conditions Box
        canvas.drawText("ENVIRONMENTAL CONDITIONS", startX, currentY, sectionTitlePaint)
        currentY += 10f
        
        val rectEnv = RectF(startX, currentY, pageWidth - startX, currentY + 50f)
        canvas.drawRoundRect(rectEnv, 8f, 8f, fillPaint)
        canvas.drawRoundRect(rectEnv, 8f, 8f, borderPaint)
        
        currentY += 18f
        canvas.drawText("TEMPERATURE (°C)", startX + 15f, currentY, labelPaint)
        canvas.drawText(report.ambientTemp.toString(), startX + 15f, currentY + 18f, valuePaint)
        
        canvas.drawText("HUMIDITY (%)", startX + 160f, currentY, labelPaint)
        canvas.drawText(report.relativeHumidity.toString(), startX + 160f, currentY + 18f, valuePaint)
        
        canvas.drawText("PRESSURE (hPa)", startX + 310f, currentY, labelPaint)
        canvas.drawText(report.atmosphericPressure.toString(), startX + 310f, currentY + 18f, valuePaint)
        
        currentY += 55f

        // Test Results Table
        canvas.drawText("TEST RESULTS SUMMARY", startX, currentY, sectionTitlePaint)
        currentY += 10f

        val results = listOf(
            "1. Weighing Performance" to (report.weighingResults.isNotEmpty() && report.weighingResults.all { it.isPass }),
            "2. Temperature Effect on No-Load Indication" to (report.tempEffectResult?.isPass == true),
            "3. Eccentricity" to (report.eccentricityResult?.isPass == true),
            "4. Discrimination & Sensitivity" to (report.discriminationResult?.isPass == true),
            "5. Repeatability" to (report.repeatabilityResult?.isPass == true),
            "6. Time-Dependence (Creep)" to (report.timeDependenceResult?.isPass == true),
            "7. Stability of Equilibrium" to (report.stabilityResult?.isPass == true),
            "8. Tilting Effect" to (report.tiltingResult?.isPass == true),
            "9. Tare Device (Weighing Test)" to (report.tareResult?.isPass == true),
            "10. Warm-up Time" to (report.warmUpResult?.isPass == true),
            "11. Voltage Variations" to (report.voltageResult?.isPass == true),
            "12. Electrical Disturbances (EMC)" to (report.emcResult?.isPass == true),
            "13. Damp Heat, Steady State" to (report.dampHeatResult?.isPass == true),
            "14. Span Stability" to (report.spanStabilityResult?.isPass == true),
            "15. Endurance" to (report.enduranceResult?.isPass == true)
        )

        // Table Header
        val thRect = RectF(startX, currentY, pageWidth - startX, currentY + 25f)
        canvas.drawRect(thRect, Paint().apply { color = accentColor; style = Paint.Style.FILL })
        canvas.drawText("TEST PROCEDURE (OIML R-76)", startX + 10f, currentY + 17f, tableHeaderPaint)
        val thResultPaint = Paint(tableHeaderPaint).apply { textAlign = Paint.Align.RIGHT }
        canvas.drawText("VERDICT", pageWidth - startX - 10f, currentY + 17f, thResultPaint)
        
        currentY += 25f
        
        // Table Rows
        var isAltRow = true
        results.forEach { (name, pass) ->
            if (isAltRow) {
                canvas.drawRect(RectF(startX, currentY, pageWidth - startX, currentY + 20f), fillPaint)
            }
            canvas.drawRect(RectF(startX, currentY, pageWidth - startX, currentY + 20f), borderPaint)
            
            canvas.drawText(name, startX + 10f, currentY + 14f, valuePaint)
            
            val text = if (pass) "PASS" else "FAIL"
            val badgeBgColor = if (pass) successColor else errorColor
            val badgeTextPaint = Paint().apply { color = Color.WHITE; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); textSize = 10f; textAlign = Paint.Align.CENTER }
            
            val badgeWidth = 45f
            val badgeLeft = pageWidth - startX - 10f - badgeWidth
            val badgeTop = currentY + 3f
            val badgeRight = pageWidth - startX - 10f
            val badgeBottom = currentY + 17f
            
            canvas.drawRoundRect(RectF(badgeLeft, badgeTop, badgeRight, badgeBottom), 4f, 4f, Paint().apply { color = badgeBgColor; style = Paint.Style.FILL })
            canvas.drawText(text, badgeLeft + (badgeWidth / 2f), currentY + 13f, badgeTextPaint)
            
            currentY += 20f
            isAltRow = !isAltRow
        }

        currentY += 30f
        
        // Final Verdict
        val isOverallPass = report.status.equals("Pass", ignoreCase = true)
        val vRect = RectF(startX, currentY, pageWidth - startX, currentY + 35f)
        val vBg = if (isOverallPass) Color.parseColor("#D1FAE5") else Color.parseColor("#FEE2E2")
        val vBorder = if (isOverallPass) Color.parseColor("#059669") else Color.parseColor("#DC2626")
        
        canvas.drawRoundRect(vRect, 8f, 8f, Paint().apply { color = vBg; style = Paint.Style.FILL })
        canvas.drawRoundRect(vRect, 8f, 8f, Paint().apply { color = vBorder; style = Paint.Style.STROKE; strokeWidth = 2f })
        
        val vTitlePaint = Paint(sectionTitlePaint).apply { color = vBorder; textSize = 16f }
        canvas.drawText("FINAL VERDICT:", startX + 15f, currentY + 23f, vTitlePaint)
        
        val vStatusPaint = Paint(vTitlePaint).apply { textAlign = Paint.Align.RIGHT; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }
        canvas.drawText(if (isOverallPass) "PASSED" else "FAILED", pageWidth - startX - 15f, currentY + 23f, vStatusPaint)

        // Signatures
        currentY += 90f
        canvas.drawLine(startX, currentY, startX + 180f, currentY, borderPaint)
        canvas.drawText("Inspector Signature", startX, currentY + 15f, labelPaint)
        
        canvas.drawLine(pageWidth - startX - 180f, currentY, pageWidth - startX, currentY, borderPaint)
        canvas.drawText("Date", pageWidth - startX - 180f, currentY + 15f, labelPaint)

        pdfDocument.finishPage(page)
    }
}

