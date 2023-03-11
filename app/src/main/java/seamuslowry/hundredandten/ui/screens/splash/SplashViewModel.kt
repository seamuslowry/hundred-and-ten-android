package seamuslowry.hundredandten.ui.screens.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import seamuslowry.hundredandten.data.AuthRepository
import javax.inject.Inject

private const val WEEK_IN_SECONDS = (7 * 12 * 60 * 60).toLong()

data class SplashData(
    val needsSignIn: Boolean,
    val autoSelect: Boolean,
)

sealed interface SplashState {
    data class Complete(val data: SplashData) : SplashState
    object Loading : SplashState
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val auth: AuthRepository,
) : ViewModel() {
    var state: SplashState by mutableStateOf(SplashState.Loading)
        private set

    init {
        checkSignedIn()
    }

    private fun checkSignedIn() {
        val token = runBlocking { auth.authToken.first() }
        val jwt = token?.let { JWT(it) }
        val expiringSoon = jwt?.isExpired(WEEK_IN_SECONDS)
        val userId = jwt?.subject
        state = SplashState.Complete(
            SplashData(
                expiringSoon == true || userId.isNullOrBlank(),
                !userId.isNullOrBlank(),
            ),
        )
    }
}
