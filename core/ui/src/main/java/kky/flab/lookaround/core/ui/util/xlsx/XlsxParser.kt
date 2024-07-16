package kky.flab.lookaround.core.ui.util.xlsx

import android.content.Context
import android.location.Address
import org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFRow

private fun XSSFRow.getCellToString(index: Int): String =
    getCell(index, CREATE_NULL_AS_BLANK).toString()

object XlsxParser {
    private val cached = mutableListOf<ParseResult>()

    fun findXY(context: Context, address: Address): ParseResult {
        if (cached.isEmpty()) {
            parseXlsx(context)
        }

        val params = getLocality(address)
        val adminArea = params[0]
        val locality = params[1]

        return cached.find { adminArea == it.adminArea && locality == it.locality }
            ?: throw IllegalArgumentException("잘못된 주소 입니다.")
    }

    private fun getLocality(address: Address): List<String> {
        val result = mutableListOf<String>()
        val adminArea = when (address.adminArea) {
            "강원도" -> "강원특별자치도"
            "제주도" -> "제주특별자치도"
            else -> address.adminArea
        }
        result.add(adminArea)
        val locality = when {
            adminArea == "서울특별시" || adminArea.endsWith("광역시") -> {
                if (address.subLocality != null) address.subLocality else adminArea
            }

            adminArea == "세종특별자치시" -> {
                adminArea
            }

            adminArea.endsWith("도") && address.subLocality != null -> {
                "${address.locality}${address.subLocality}"
            }

            else -> address.locality
        }
        result.add(locality)

        return result
    }

    private fun parseXlsx(context: Context) {
        val assetManager = context.resources.assets
        val inputStream = assetManager.open("weather.xlsx")
        val workBook = WorkbookFactory.create(inputStream)
        val sheet = workBook.getSheetAt(0)

        val rowIterator = sheet.rowIterator()

        var rowIndex = 0

        while (rowIterator.hasNext()) {
            val row = rowIterator.next() as XSSFRow
            if (rowIndex > 0) {
                val locality = row.getCellToString(3)
                val subLocality = row.getCellToString(4)
                //3단계 주소가 비어있는 경우만 캐싱한다.
                if (locality.isNotBlank() && subLocality.isBlank()) {
                    val result = ParseResult(
                        adminArea = row.getCellToString(2),
                        locality = row.getCellToString(3),
                        nx = row.getCellToString(5).toInt(),
                        ny = row.getCellToString(6).toInt(),
                    )
                    cached.add(result)
                }
            }
            rowIndex++
        }
    }
}
