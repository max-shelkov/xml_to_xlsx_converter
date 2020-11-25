import retrofit2.Call
import retrofit2.http.GET

interface OffersAPI {

    @GET("realty-feed")
    fun getRealityOffers() : Call<MutableList<Offer>>

}