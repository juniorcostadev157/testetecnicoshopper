import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConfirmRideRequest(
    val customer_id: String,
    val origin: String,
    val destination: String,
    val distance: Int,
    val duration: String,
    val driver: Motorista,
    val value: Double
) : Parcelable

@Parcelize
data class ConfirmRideResponse(
    val success: Boolean
) : Parcelable
