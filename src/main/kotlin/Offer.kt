import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "offer")
class Offer @JvmOverloads constructor(

    @field: Element(name = "type", required = false)
    var type : String = "",

    @field: Element(name = "category", required = false)
    var category : String = "",

    @field: Element(name = "region", required = false)
    var region : String = "",

    @field: Element(name = "locality-name", required = false)
    var localityName : String = "",

    @field: Element(name = "locality-name", required = false)
    var address : String = "",

    @field: Element(name = "rooms", required = false)
    var rooms : String = "",

    @field: Element(name = "floor", required = false)
    var floor : String = "",

    @field: Element(name = "floors-total", required = false)
    var floorsTotal : String = "",

    @field: Element(name = "description", required = false)
    var description : String = "",
)