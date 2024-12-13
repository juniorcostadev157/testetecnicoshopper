import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Motorista(
    val id: Int,
    val name: String,
    val description: String,
    val vehicle: String,
    val review: Review, // Adiciona o objeto `review`
    val value: Double
) : Parcelable

@Parcelize
data class Review( // Novo modelo para o review
    val rating: Double,
    val comment: String
) : Parcelable
