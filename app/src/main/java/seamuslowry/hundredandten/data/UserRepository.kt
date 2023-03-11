package seamuslowry.hundredandten.data

import seamuslowry.hundredandten.sources.UserSource
import seamuslowry.hundredandten.sources.models.GoogleUserRequest
import javax.inject.Inject

interface UserRepository {
    suspend fun getAccessToken(idToken: String): String
    suspend fun getMe() // TODO get rid of
    suspend fun logout()
}

class NetworkUserRepository @Inject constructor(
    private val source: UserSource,
) : UserRepository {
    override suspend fun getAccessToken(idToken: String): String {
        val response = source.getGoogleUser(GoogleUserRequest(idToken))
        return response.authenticationToken
    }

    override suspend fun logout() {
        source.logout()
    }

    override suspend fun getMe() {
        try {
            source.getMe()
        } catch (e: Exception) {
            print(e)
        }
    }
}
