import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root


@Root(name = "realty-feed", strict = false)
class Offers @JvmOverloads constructor(

    @field: ElementList(inline = true)
    var offers: MutableList<Offer> = mutableListOf<Offer>()

)