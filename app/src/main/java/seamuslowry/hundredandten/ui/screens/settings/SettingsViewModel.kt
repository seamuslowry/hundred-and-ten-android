package seamuslowry.hundredandten.ui.screens.settings

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.lifecycle.HiltViewModel
import seamuslowry.hundredandten.data.AuthRepository
import seamuslowry.hundredandten.data.UserRepository
import javax.inject.Inject

sealed interface SettingsState {
    object LoggedOut : SettingsState
    object Loading : SettingsState
    object Error : SettingsState
    object Waiting : SettingsState
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: UserRepository,
    private val auth: AuthRepository,
    private val application: Application,
) : ViewModel() {
    var state: SettingsState by mutableStateOf(SettingsState.Waiting)
        private set

    private val client by lazy {
        Identity.getSignInClient(application)
    }

    suspend fun signOut() {
        try {
            state = SettingsState.Loading
            repo.logout()
            auth.clear()
            client.signOut()
            state = SettingsState.LoggedOut
        } catch (e: Exception) {
            state = SettingsState.Error
        }
    }
}
