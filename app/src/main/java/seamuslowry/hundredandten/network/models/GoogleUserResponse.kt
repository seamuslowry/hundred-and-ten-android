package seamuslowry.hundredandten.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseUser(
    @SerialName("authenticationToken") val authenticationToken: String,
)

@Serializable
data class GoogleUserResponse(
    @SerialName("authenticationToken") val authenticationToken: String,
    @SerialName("user") val user: ResponseUser,
)
