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

enum class SettingsBehavior {
    LOAD_USER,
    SAVE_USER,
    LOGOUT,
}

data class SettingsState(
    val user: User = User(""),
    val loading: Set<SettingsBehavior> = emptySet(),
    val errors: Set<SettingsBehavior> = emptySet(),
    val loggedOut: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: UserRepository,
    private val auth: AuthRepository,
    private val application: Application,
) : ViewModel() {
    var state: SettingsState by mutableStateOf(
        SettingsState(
            loading = setOf(SettingsBehavior.LOAD_USER),
        ),
    )

    private val client by lazy {
        Identity.getSignInClient(application)
    }

    init {
        viewModelScope.launch {
            state = state.copy(
                loading = state.loading.minus(SettingsBehavior.LOAD_USER),
                errors = state.errors.minus(SettingsBehavior.LOAD_USER),
                user = auth.user
                    .filterNotNull()
                    .first(),
            )
        }
    }

    fun updateUser(user: User) {
        state = state.copy(user = user)
    }

    suspend fun saveUser() {
        state = state.copy(
            loading = state.loading.plus(SettingsBehavior.SAVE_USER),
            errors = state.errors.minus(SettingsBehavior.LOAD_USER),
        )
        try {
            val newUser = repo.update(state.user.name, state.user.pictureUrl)
            auth.saveUser(newUser)
            state = state.copy(user = newUser)
        } catch (e: Exception) {
            state = state.copy(errors = state.errors.plus(SettingsBehavior.SAVE_USER))
        } finally {
            state = state.copy(loading = state.loading.minus(SettingsBehavior.SAVE_USER))
        }
    }

    suspend fun signOut() {
        state = state.copy(
            loading = state.loading.plus(SettingsBehavior.LOGOUT),
            errors = state.errors.minus(SettingsBehavior.LOGOUT),
        )
        try {
            repo.logout()
            auth.clear()
            client.signOut()
            state = state.copy(loggedOut = true)
        } catch (e: Exception) {
            state = state.copy(errors = state.errors.plus(SettingsBehavior.LOGOUT))
        } finally {
            state = state.copy(loading = state.loading.minus(SettingsBehavior.SAVE_USER))
        }
    }
}
