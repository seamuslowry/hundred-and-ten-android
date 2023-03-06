package seamuslowry.hundredandten.sources

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import seamuslowry.hundredandten.sources.models.GoogleUserRequest
import seamuslowry.hundredandten.sources.models.GoogleUserResponse

interface UserSource {
    suspend fun getGoogleUser(request: GoogleUserRequest, accessType: String): GoogleUserResponse

    suspend fun getMe(
        token: String,
    ): NotImportant

    suspend fun getRefresh(
        token: String,
    ): NotImportant

    suspend fun logout(
        token: String,
    )
}

@kotlinx.serialization.Serializable
data class NotImportant(val user_id: String)

interface NetworkUserSource : UserSource {
    @POST(".auth/login/google")
    override suspend fun getGoogleUser(
        @Body request: GoogleUserRequest,
        @Query("access_type") accessType: String,
    ): GoogleUserResponse

    @GET(".auth/me")
    override suspend fun getMe(
        @Header("X-ZUMO-AUTH") token: String,
    ): NotImportant

    @GET(".auth/refresh")
    override suspend fun getRefresh(
        @Header("X-ZUMO-AUTH") token: String,
    ): NotImportant

    // TODO verify works
    @GET(".auth/logout")
    override suspend fun logout(
        @Header("X-ZUMO-AUTH") token: String,
    )
}
