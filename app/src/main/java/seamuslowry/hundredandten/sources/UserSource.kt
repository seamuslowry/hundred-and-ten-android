package seamuslowry.hundredandten.sources

import retrofit2.http.Body
import retrofit2.http.POST
import seamuslowry.hundredandten.sources.models.GoogleUserRequest
import seamuslowry.hundredandten.sources.models.GoogleUserResponse

interface UserSource {
    suspend fun getGoogleUser(request: GoogleUserRequest): GoogleUserResponse
}

interface NetworkUserSource : UserSource {
    @POST(".auth/login/google")
    override suspend fun getGoogleUser(@Body request: GoogleUserRequest): GoogleUserResponse
}
