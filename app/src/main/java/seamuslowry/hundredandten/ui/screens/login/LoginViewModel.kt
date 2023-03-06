package seamuslowry.hundredandten.ui.screens.login

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import seamuslowry.hundredandten.data.UserRepository
import seamuslowry.hundredandten.models.User
import javax.inject.Inject

private const val TAG = "LoginViewModel"

sealed interface AppLoginState {
    data class Success(val user: User) : AppLoginState
    object Loading : AppLoginState
    object Error : AppLoginState
    object Unused : AppLoginState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: UserRepository,
    private val application: Application,
) : ViewModel() {
    var appLoginState: AppLoginState by mutableStateOf(AppLoginState.Unused)
        private set

    private fun getSignInClient(clientId: String) = GoogleSignIn.getClient(
        application,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(clientId)
            .requestServerAuthCode(clientId)
            .build(),
    )

    fun startGoogleSignIn(
        clientId: String,
        launchActivityResult: (Intent) -> Unit,
    ) {
        appLoginState = AppLoginState.Loading

        // TODO use GoogleSignIn.getLastSignedInAccount(this);

        launchActivityResult(getSignInClient(clientId).signInIntent)
    }

    fun handleGoogleSignInResult(
        result: ActivityResult,
    ) {
        when (result.resultCode) {
            ComponentActivity.RESULT_OK -> {
                try {
                    val signInData = GoogleSignIn.getSignedInAccountFromIntent(result.data).result

                    val idToken = signInData.idToken ?: run {
                        appLoginState = AppLoginState.Error // TODO error message
                        return@handleGoogleSignInResult
                    }
                    val authorizationCode = signInData.serverAuthCode ?: run {
                        appLoginState = AppLoginState.Error // TODO error message
                        return@handleGoogleSignInResult
                    }

                    viewModelScope.launch {
                        appLoginState = try {
                            val user = repo.getFromCredentials(idToken, authorizationCode)
                            repo.getMe(user.pictureUrl)
                            repo.getRefresh(user.pictureUrl)
                            AppLoginState.Success(user)
                        } catch (e: Exception) {
                            AppLoginState.Error
                        }
                    }
                } catch (e: ApiException) {
                    Log.e(TAG, "Sign-in failed with error code:", e)
                    appLoginState = AppLoginState.Error // TODO error message
                }
            }
            else -> {
                Log.e(TAG, "Sign-in failed")
                appLoginState = AppLoginState.Error // TODO error message
            }
        }
    }

    fun signOut(clientId: String) {
        // TODO make this not just a bad proof of concept
        val currentState = appLoginState
        if (currentState !is AppLoginState.Success) {
            return
        }

        viewModelScope.launch {
            repo.logout(currentState.user.pictureUrl)
        }
        getSignInClient(clientId).signOut()
        appLoginState = AppLoginState.Unused
    }
}
