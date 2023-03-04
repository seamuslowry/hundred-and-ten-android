package seamuslowry.hundredandten.data

import seamuslowry.hundredandten.models.User
import seamuslowry.hundredandten.sources.UserSource
import seamuslowry.hundredandten.sources.models.GoogleUserRequest
import javax.inject.Inject

interface UserRepository {
    suspend fun getFromIdToken(idToken: String): User
}

class NetworkUserRepository @Inject constructor(
    private val source: UserSource,
) : UserRepository {
    override suspend fun getFromIdToken(idToken: String): User {
        val response = source.getGoogleUser(GoogleUserRequest(idToken))
        return User(response.user.userId, "")
    }
}
