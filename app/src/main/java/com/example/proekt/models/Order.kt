import com.example.proekt.models.Basket
import com.google.gson.annotations.SerializedName
import java.util.Date

class Order(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("user_Id")
    val userId: Int,

    @SerializedName("Full_name")
    val fullName: String? = null,

    @SerializedName("order")
    val order: List<Basket> = emptyList(),

    @SerializedName("date")
    val date: Date? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("adress")
    val adress: String,

    @SerializedName("type_order")
    val typeOrder: String,

    @SerializedName("type_pay")
    val typePay: String,

    @SerializedName("description")
    val description: String
)