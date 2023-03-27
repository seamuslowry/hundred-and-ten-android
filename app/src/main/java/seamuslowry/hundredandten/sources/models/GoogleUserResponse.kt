package seamuslowry.hundredandten.sources.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseUser(
    @SerialName("userId") val userId: String,
)

@Serializable
data class GoogleUserResponse(
    @SerialName("authenticationToken") val authenticationToken: String,
    @SerialName("user") val user: ResponseUser,
)
