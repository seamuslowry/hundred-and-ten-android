package seamuslowry.hundredandten.network

import retrofit2.http.Body
import retrofit2.http.POST
import seamuslowry.hundredandten.network.models.GoogleUserRequest
import seamuslowry.hundredandten.network.models.GoogleUserResponse

interface GameApi {
    @POST(".auth/login/google")
    suspend fun getGoogleUser(@Body request: GoogleUserRequest): GoogleUserResponse
}
