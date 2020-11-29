import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PoiUtils{
    companion object{


        fun XSSFCell.getDate(pattern: String = "dd.MM.yyyy") : LocalDate?{
//            "dd.MM.yyyy"
            return when (this.cellType){
                CellType.STRING -> LocalDate.parse(this.stringCellValue, DateTimeFormatter.ofPattern(pattern))
                CellType.NUMERIC -> LocalDate.from(this.localDateTimeCellValue)
                else -> null
            }
        }

        fun XSSFCell.getString() : String =
            if(this.cellType == CellType.STRING) this.stringCellValue else ""




        fun saveOutFile(workbook: XSSFWorkbook, path: String) {
            try {
                val fileOut = FileOutputStream(path)
                workbook.write(fileOut)
                fileOut.close()
//                workbook.close() //если активировать, сохраняет внесенные изменения в исходном файле
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun XSSFSheet.asCSV(delimeter: String) : String{
            val res = StringBuilder()
            for (i in 0 .. this.lastRowNum) {
                val row = this.getRow(i)
                for (j in 0 until row.lastCellNum-1) {
                    res.append(row.getCell(j)?.stringCellValue?:"").append(delimeter)
                }
                res.append(row.getCell(row.lastCellNum-1)?.stringCellValue?:"").append("\n")
            }

            return res.toString()
        }
    }
}