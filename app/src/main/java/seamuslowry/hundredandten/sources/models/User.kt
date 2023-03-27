package seamuslowry.hundredandten.sources.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("identifier") val id: String,
    @SerialName("name") val name: String = "",
    @SerialName("picture_url") val pictureUrl: String = "",
)
