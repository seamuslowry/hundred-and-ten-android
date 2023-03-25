package seamuslowry.hundredandten.ui.screens.settings

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import seamuslowry.hundredandten.data.AuthRepository
import seamuslowry.hundredandten.data.UserRepository
import seamuslowry.hundredandten.sources.models.User
import javax.inject.Inject

enum class SettingsError {
    SAVE_USER,
    LOGOUT,
}

sealed interface SettingsState {
    object LoggedOut : SettingsState
    data class Loading(val user: User = User("")) : SettingsState
    data class Error(val user: User = User(""), val error: SettingsError) : SettingsState
    data class Editable(
        val user: User,
    ) : SettingsState
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: UserRepository,
    private val auth: AuthRepository,
    private val application: Application,
) : ViewModel() {
    var state: SettingsState by mutableStateOf(SettingsState.Loading())
        private set

    private val client by lazy {
        Identity.getSignInClient(application)
    }

    init {
        viewModelScope.launch {
            state = SettingsState.Editable(
                auth.user
                    .filterNotNull()
                    .first(),
            )
        }
    }

    fun updateUser(user: User) {
        state = SettingsState.Editable(user)
    }

    suspend fun saveUser() {
        val currentState = state
        if (currentState is SettingsState.Editable) {
            try {
                state = SettingsState.Loading(currentState.user)
                val newUser = repo.update(currentState.user.name, currentState.user.pictureUrl)
                auth.saveUser(newUser)
                state = SettingsState.Editable(newUser)
            } catch (e: Exception) {
                state = SettingsState.Error(currentState.user, SettingsError.SAVE_USER)
            }
        }
    }

    suspend fun signOut() {
        val currentState = state
        if (currentState !is SettingsState.Editable) return

        try {
            state = SettingsState.Loading(currentState.user)
            repo.logout()
            auth.clear()
            client.signOut()
            state = SettingsState.LoggedOut
        } catch (e: Exception) {
            state = SettingsState.Error(currentState.user, SettingsError.LOGOUT)
        }
    }
}
