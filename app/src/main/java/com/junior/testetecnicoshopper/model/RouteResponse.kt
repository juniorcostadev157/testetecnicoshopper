import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteResponse(
    val distanceMeters: Int = 0,
    val duration: String? = null,
    val polyline: Polyline? = null,
    val startLocation: LatLng? = null,
    val endLocation: LatLng? = null,
    val routes: List<Route>? = null
) : Parcelable

@Parcelize
data class Route(
    val legs: List<Leg>
) : Parcelable

@Parcelize
data class Leg(
    val distanceMeters: Int = 0,
    val duration: String? = null,
    val startLocation: LatLng,
    val endLocation: LatLng,
    val polyline: Polyline?
) : Parcelable

@Parcelize
data class Polyline(
    val encodedPolyline: String
) : Parcelable
