package seamuslowry.hundredandten

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import seamuslowry.hundredandten.data.AuthRepository
import javax.inject.Inject

private const val WEEK_IN_SECONDS = (7 * 12 * 60 * 60).toLong()
sealed interface MainActivityState {
    object LoggedIn : MainActivityState
    object ReSignIn : MainActivityState
    object NewUser : MainActivityState
    object Loading : MainActivityState
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    auth: AuthRepository,
) : ViewModel() {
    val state: StateFlow<MainActivityState> = auth.authToken.map {
        val jwt = it?.let { JWT(it) }
        val expiringSoon = jwt?.isExpired(WEEK_IN_SECONDS)
        val userId = jwt?.subject

        when {
            expiringSoon == true -> MainActivityState.ReSignIn
            !userId.isNullOrBlank() -> MainActivityState.LoggedIn
            else -> MainActivityState.NewUser
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainActivityState.Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )
}
