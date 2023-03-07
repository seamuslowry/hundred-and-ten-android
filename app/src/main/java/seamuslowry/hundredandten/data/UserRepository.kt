package seamuslowry.hundredandten.data

import seamuslowry.hundredandten.sources.UserSource
import seamuslowry.hundredandten.sources.models.GoogleUserRequest
import javax.inject.Inject

interface UserRepository {
    suspend fun getAccessToken(idToken: String): String
    suspend fun getMe(authToken: String) // TODO get rid of

//    suspend fun getRefresh(authToken: String) // TODO return
    suspend fun logout(authToken: String)
}

class NetworkUserRepository @Inject constructor(
    private val source: UserSource,
) : UserRepository {
    override suspend fun getAccessToken(idToken: String): String {
        val response = source.getGoogleUser(GoogleUserRequest(idToken))
        return response.authenticationToken
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

//    override suspend fun getRefresh(authToken: String) {
//        try {
//            source.getRefresh(authToken)
//        } catch (e: Exception) {
//            print(e)
//        }
//    }
}
