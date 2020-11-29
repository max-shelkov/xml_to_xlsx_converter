import PoiUtils.Companion.asCSV
import javafx.application.Application
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.stage.Stage
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.xml.parsers.DocumentBuilderFactory


fun main() {
    Application.launch(AppWindow::class.java)
}

class AppWindow : Application(){

    @FXML lateinit var urlTextField : TextField
    @FXML lateinit var infoLabel    : Label

    override fun start(primaryStage: Stage?) {
        primaryStage?.let {
            it.scene = Scene(FXMLLoader.load(javaClass.getResource("main_window.fxml")))
            it.isResizable = false
            it.title = "Эльфидэль: Конвертер файлов"
            it.show()
        }
    }

    @FXML
    fun initialize() {

    }

    fun onFileConvert(){
        println("button pressed")
        infoLabel.text = ""

/*
        //для работы с локальным XML файлом
        // Создается построитель документа
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        // Создается дерево DOM документа из файла
        val document: Document = documentBuilder.parse("feed.xml")
*/


        //для работы с XML по ссылке
//        val pre_apiURL = "https://2bishop.ru/xml/yandex/53003_60.xml"
        var document : Document? = null
        try {
            val pre_apiURL = urlTextField.text
            println("url $pre_apiURL")
            val url = URL(pre_apiURL)
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            document = db.parse(url.openStream())
        }catch (e : FileNotFoundException){
            infoLabel.text = "Указана неверная ссылка. Файл не найден."
            println("Указана неверная ссылка. Файл не найден.")
            return
        }


        val root: Node = document!!.documentElement
        val offers = root.childNodes
        var workbook : XSSFWorkbook? = null
        try {
//            workbook = openXlsxTemplate("src\\main\\resources\\Price_2GIS_Template.xlsx")
            workbook = openXlsxTemplate("Price_2GIS_Template.xlsx")
        }catch (e : Exception){
            addInfoText("не найден файл шаблона")
            println("не найден файл шаблона")
            return
        }
        val sheet = workbook.getSheetAt(0)
        println("------------------------------------")

        for (i in 0 until offers.length) {
            val offer = offers.item(i)
            val rowNum = sheet.lastRowNum+1
            if (offer.nodeType != Node.TEXT_NODE){
                val offerProps = offer.childNodes
                var name = ""
                var category = ""
                var commercialType = ""
                var rooms = ""
                var address = ""
                var floor = ""
                var floors = ""
                var lotArea = ""
                var areaFull = ""
                var areaLiving = ""
                for (j in 0 until  offerProps.length ){
                    //достаем данные из XML
                    val prop = offerProps.item(j)
                    if (prop.nodeType != Node.TEXT_NODE){
                        when(prop.nodeName){
                            "type" -> {
                                sheet.putStringToCell(rowNum, 2, prop.getText())
                            }
                            "category" -> {
                                category = prop.getText()
                            }
                            "commercial-type" -> {
                                commercialType = prop.getCommercialType(commercialType)
                            }
                            "location" -> {
                                address = prop.getAddress()
                            }
                            "price" -> {
                                sheet.putStringToCell(rowNum, 1, prop.getPrice())
                            }
                            "rooms" -> {
                                rooms = prop.getText()
                            }
                            "floor" -> {
                                floor = prop.getText()
                            }
                            "floors-total" -> {
                                floors = prop.getText()
                            }
                            "description" -> {
                                sheet.putStringToCell(rowNum, 3, prop.getText())
                            }
                            "lot-area" -> {
                                lotArea = prop.getArea()
                            }
                            "area" -> {
                                areaFull = prop.getArea()
                            }
                            "living-space" -> {
                                areaLiving = prop.getArea()
                            }
                            "kitchen-space" -> {
                            }
                        }
                    }
                }
                val sb = StringBuilder()
                name = when(category.toLowerCase()){
                    "квартира" -> {
                        if (rooms.isNotEmpty()) sb.append("$rooms-к кв. ")
                        if (address.isNotEmpty()) sb.append("$address ")
                        if (floor.isNotEmpty()) sb.append("эт. $floor")
                        if (floor.isNotEmpty() && floors.isNotEmpty()) sb.append("/$floor ")
                        if (areaFull.isNotEmpty()) sb.append("S:$areaFull")
                        if (areaFull.isNotEmpty() && areaLiving.isNotEmpty()) sb.append("/$areaLiving")
                        sb.toString()
                    }
                    "комната" -> {
                        sb.append("Комната. ")
                        if (address.isNotEmpty()) sb.append("$address ")
                        if (floor.isNotEmpty()) sb.append("эт. $floor")
                        if (floor.isNotEmpty() && floors.isNotEmpty()) sb.append("/$floor ")
                        if (areaFull.isNotEmpty()) sb.append("S:$areaFull ")
                        sb.toString()
                    }
                    "участок" -> {
                        sb.append("Участок. ")
                        if (address.isNotEmpty()) sb.append("$address ")
                        if (lotArea.isNotEmpty()) sb.append("S:$lotArea ")
                        sb.toString()
                    }
                    "дом" -> {
                        sb.append("Дом. ")
                        if (address.isNotEmpty()) sb.append("$address ")
                        if (areaFull.isNotEmpty()) sb.append("S дома:$areaFull ")
                        if (lotArea.isNotEmpty()) sb.append("S участка:$lotArea ")
                        sb.toString()
                    }
                    "коммерческая" -> {
                        if (commercialType.isNotEmpty()) sb.append("$commercialType ")
                        if (address.isNotEmpty()) sb.append("$address ")
                        if (floor.isNotEmpty()) sb.append("этаж $floor ")
                        if (areaFull.isNotEmpty()) sb.append("S пом.:$areaFull ")
                        if (lotArea.isNotEmpty()) sb.append("S уч.:$lotArea ")
                        sb.toString()
                    }
                    else -> ""
                }
                if (name.isNotEmpty()) sheet.putStringToCell(rowNum, 0, name.reduce(147))


            }
//            addInfoText("$i позиций обработано")
        }

        try {
            val s = workbook.getSheetAt(0).asCSV(";")
            val f = File("offers_as.csv")
            f.writeText(s, Charsets.UTF_8)

            PoiUtils.saveOutFile(workbook, "offers.xlsx")
        }catch (e : Exception){
            addInfoText("Не удалось сохранить файлы на диск. Проверьте разрешение на запись в папку.")
            println("Не удалось сохранить файлы на диск. Проверьте разрешение на запись в папку.")
            return
        }
        println("converting finished ")
        addInfoText("выгрузка завершена")
    }

    private fun XSSFSheet.putStringToCell(r: Int, c: Int, data: String){
        val row = this.getRow(r)?:let {
            val rw = this.createRow(r)
//            for (i in 0 .. 3) rw.createCell(i).setCellValue("")
            rw
        }

        val cell = row.getCell(c)?:row.createCell(c)
        cell.cellType = CellType.STRING
        cell.setCellValue(data)
    }

    private fun Node.getText() : String{
        return this.childNodes.item(0)?.textContent?:"не указано"
    }

    private fun Node.getCommercialType(str: String) : String{
        val txt = when (this.textContent) {
            "office" -> "Офис. "
            "free purpose" -> "Свободного назначения"
            "public catering" -> "Для общепита"
            else -> {
                ""
            }
        }
        return if (str.isEmpty()) "Коммерческая. $txt" else "$str / $txt"
    }

    private fun Node.getAddress() : String{
        val adr = StringBuilder()
        val items = this.childNodes
        for (i in 0 until items.length){
            val param = items.item(i)
            if (param.nodeType != Node.TEXT_NODE){
                when(param.nodeName){
//                    "country",
//                    "region",
                    "locality-name",
                    "sub-locality-name",
                    "address" -> param.childNodes.item(0)?.let { adr.append(it.textContent).append(", ") }
                }

            }

        }
        return adr.toString()
    }

    private fun Node.getPrice() : String{
        val price = StringBuilder()
        val items = this.childNodes
        for (i in 0 until items.length){
            val param = items.item(i)
            if (param.nodeType != Node.TEXT_NODE){
                when(param.nodeName){
                    "value" -> param.childNodes.item(0)?.let { price.append(it.textContent) }
/*
                    "currency" -> param.childNodes.item(0)?.let {
                        price.append(
                                when(it.textContent){
                                    "RUR" -> "Руб."
                                    "USD" -> "Долларов США"
                                    "EUR" -> "Евро"
                                    else -> it.textContent
                                }
                        )
                    }
*/
                }

            }

        }
        return price.toString()
    }

    private fun Node.getArea() : String{
        val sb = StringBuilder()
        val items = this.childNodes
        for (i in 0 until items.length){
            val param = items.item(i)
            if (param.nodeType != Node.TEXT_NODE){
                when(param.nodeName){
                    "value" -> param.childNodes.item(0)?.let { sb.append(it.textContent).append(" ") }
                    "unit" -> param.childNodes.item(0)?.let { sb.append(it.textContent) }
                }

            }

        }
        return sb.toString()
    }

    private fun String.reduce(length: Int) : String {
        val res = this.removeSurrounding(" ")
        return if (res.length > length) "${res.removeRange(length, res.length)}..."
        else res
    }

    private fun openXlsxTemplate(filePath: String) : XSSFWorkbook{
        val x = javaClass.getResourceAsStream(filePath)

        return WorkbookFactory.create(x) as XSSFWorkbook
//        return WorkbookFactory.create(File(filePath)) as XSSFWorkbook
    }

    private fun addInfoText(info : String){
        infoLabel.text = "${infoLabel.text}\n$info"
    }
}