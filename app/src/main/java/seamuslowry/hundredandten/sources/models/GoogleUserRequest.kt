package seamuslowry.hundredandten.sources.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleUserRequest(
    @SerialName("id_token") val idToken: String,
)
