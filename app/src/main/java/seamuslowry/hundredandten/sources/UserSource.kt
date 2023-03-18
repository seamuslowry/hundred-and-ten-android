package seamuslowry.hundredandten.sources

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import seamuslowry.hundredandten.sources.models.GoogleUserRequest
import seamuslowry.hundredandten.sources.models.GoogleUserResponse
import seamuslowry.hundredandten.sources.models.User

interface UserSource {
    suspend fun getGoogleUser(request: GoogleUserRequest): GoogleUserResponse

    suspend fun login(request: User): User

    suspend fun logout()
}

interface NetworkUserSource : UserSource {
    @POST(".auth/login/google")
    override suspend fun getGoogleUser(
        @Body request: GoogleUserRequest,
    ): GoogleUserResponse

    @PUT("api/self")
    override suspend fun login(
        @Body request: User,
    ): User

    @GET(".auth/logout")
    override suspend fun logout()
}
