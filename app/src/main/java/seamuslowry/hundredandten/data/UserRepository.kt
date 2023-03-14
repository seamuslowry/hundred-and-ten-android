package seamuslowry.hundredandten.data

import seamuslowry.hundredandten.sources.UserSource
import seamuslowry.hundredandten.sources.models.GoogleUserRequest
import seamuslowry.hundredandten.sources.models.GoogleUserResponse
import javax.inject.Inject

interface UserRepository {
    suspend fun loginWithGoogle(idToken: String): GoogleUserResponse
    suspend fun logout()
}

class NetworkUserRepository @Inject constructor(
    private val source: UserSource,
) : UserRepository {
    override suspend fun loginWithGoogle(idToken: String) = source.getGoogleUser(GoogleUserRequest(idToken))

    override suspend fun logout() {
        source.logout()
    }
}
