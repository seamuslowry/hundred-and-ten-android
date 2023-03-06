package seamuslowry.hundredandten.data

import seamuslowry.hundredandten.models.User
import seamuslowry.hundredandten.sources.UserSource
import seamuslowry.hundredandten.sources.models.GoogleUserRequest
import javax.inject.Inject

interface UserRepository {
    suspend fun getFromIdToken(idToken: String): User
    suspend fun getMe(authToken: String)
    suspend fun getRefresh(authToken: String)
}

class NetworkUserRepository @Inject constructor(
    private val source: UserSource,
) : UserRepository {
    override suspend fun getFromIdToken(idToken: String): User {
        val response = source.getGoogleUser(GoogleUserRequest(idToken), "offline")
        return User(response.user.userId, response.authenticationToken)
    }

    override suspend fun getMe(authToken: String) {
        try {
            source.getMe(authToken)
        } catch (e: Exception) {
            print(e)
        }
    }

    override suspend fun getRefresh(authToken: String) {
        try {
            source.getRefresh(authToken)
        } catch (e: Exception) {
            print(e)
        }
    }
}
