package seamuslowry.hundredandten.sources

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import seamuslowry.hundredandten.sources.models.GoogleUserRequest
import seamuslowry.hundredandten.sources.models.GoogleUserResponse

interface UserSource {
    suspend fun getGoogleUser(request: GoogleUserRequest): GoogleUserResponse

    suspend fun getMe(): NotImportant

    suspend fun logout()
}

@kotlinx.serialization.Serializable
data class NotImportant(val user_id: String)

interface NetworkUserSource : UserSource {
    @POST(".auth/login/google")
    override suspend fun getGoogleUser(
        @Body request: GoogleUserRequest,
    ): GoogleUserResponse

    @GET(".auth/me")
    override suspend fun getMe(): NotImportant

    @GET(".auth/logout")
    override suspend fun logout()
}
