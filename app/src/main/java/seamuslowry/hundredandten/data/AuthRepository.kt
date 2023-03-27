package seamuslowry.hundredandten.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import seamuslowry.hundredandten.sources.models.User
import javax.inject.Inject

private const val AUTH_STORE = "authenticationStore"
private const val TOKEN_KEY = "authToken"
private const val NAME_KEY = "name"
private const val PICTURE_KEY = "picture"
private const val ID_KEY = "id"

class AuthRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = AUTH_STORE)
        val AUTH_TOKEN = stringPreferencesKey(TOKEN_KEY)
        val DISPLAY_NAME = stringPreferencesKey(NAME_KEY)
        val PICTURE_URL = stringPreferencesKey(PICTURE_KEY)
        val ID = stringPreferencesKey(ID_KEY)
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit {
            it[AUTH_TOKEN] = token
        }
    }

    suspend fun saveUser(user: User) {
        context.dataStore.edit {
            it[DISPLAY_NAME] = user.name
            it[PICTURE_URL] = user.pictureUrl
            it[ID] = user.id
        }
    }

    suspend fun clear() {
        context.dataStore.edit {
            it.clear()
        }
    }

    val authToken: Flow<String?> = context.dataStore.data
        .map { it[AUTH_TOKEN] }

    val user: Flow<User?> = context.dataStore.data
        .map {
            val id = it[ID] ?: return@map null
            val name = it[DISPLAY_NAME] ?: return@map null
            val pictureUrl = it[PICTURE_URL] ?: ""

            User(id, name, pictureUrl)
        }
}
