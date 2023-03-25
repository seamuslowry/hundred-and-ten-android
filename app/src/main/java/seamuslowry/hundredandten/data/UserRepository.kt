package seamuslowry.hundredandten.data

import seamuslowry.hundredandten.sources.UserSource
import seamuslowry.hundredandten.sources.models.GoogleUserRequest
import seamuslowry.hundredandten.sources.models.GoogleUserResponse
import seamuslowry.hundredandten.sources.models.User
import javax.inject.Inject

interface UserRepository {
    suspend fun loginWithGoogle(idToken: String): GoogleUserResponse

    suspend fun login(name: String, pictureUrl: String): User

    suspend fun update(name: String, pictureUrl: String): User

    suspend fun logout()
}

class NetworkUserRepository @Inject constructor(
    private val source: UserSource,
) : UserRepository {
    override suspend fun loginWithGoogle(idToken: String) = source.getGoogleUser(GoogleUserRequest(idToken))

    override suspend fun login(name: String, pictureUrl: String) = source.login(User("", name, pictureUrl))

    override suspend fun update(name: String, pictureUrl: String) = source.updateSelf(User("", name, pictureUrl))

    override suspend fun logout() {
        source.logout()
    }
}
