import javafx.application.Application
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

fun main() {
    Application.launch(AppWindow::class.java)
}

class AppWindow : Application(){
    override fun start(primaryStage: Stage?) {
        primaryStage?.let {
            it.scene = Scene(FXMLLoader.load(javaClass.getResource("main_window.fxml")))
            it.isResizable = false
            it.title = "конвертер файлов"
            it.show()
        }
    }


    @FXML
    fun initialize() {


        val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl("https://2bishop.ru/aparts/3713_53003/")
                .client(OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
        val offersAPI = retrofit.create(OffersAPI::class.java)
        val ofs = offersAPI.getRealityOffers()
        println(ofs)
    }
}