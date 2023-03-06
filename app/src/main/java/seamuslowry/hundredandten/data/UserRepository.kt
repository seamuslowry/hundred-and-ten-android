package seamuslowry.hundredandten.data

import seamuslowry.hundredandten.models.User
import seamuslowry.hundredandten.sources.UserSource
import seamuslowry.hundredandten.sources.models.GoogleUserRequest
import javax.inject.Inject

interface UserRepository {
    suspend fun getFromCredentials(idToken: String, authorizationCode: String): User
    suspend fun getMe(authToken: String) // TODO get rid of
    suspend fun getRefresh(authToken: String) // TODO return
    suspend fun logout(authToken: String)
}

class NetworkUserRepository @Inject constructor(
    private val source: UserSource,
) : UserRepository {
    override suspend fun getFromCredentials(idToken: String, authorizationCode: String): User {
        val response = source.getGoogleUser(GoogleUserRequest(idToken, authorizationCode))
        return User(response.user.userId, response.authenticationToken)
    }

    override suspend fun logout(authToken: String) {
        source.logout(authToken)
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
