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
import javax.inject.Inject

private const val AUTH_STORE = "authenticationStore"
private const val TOKEN_KEY = "authToken"

class AuthRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = AUTH_STORE)
        val AUTH_TOKEN = stringPreferencesKey(TOKEN_KEY)
    }
    suspend fun saveToken(token: String) {
        context.dataStore.edit {
            it[AUTH_TOKEN] = token
        }
    }

    suspend fun clear() {
        context.dataStore.edit {
            it.clear()
        }
    }

    val authToken: Flow<String?> = context.dataStore.data
        .map { it[AUTH_TOKEN] }
}
