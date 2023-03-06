package seamuslowry.hundredandten.ui.screens.login

import android.app.Application
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
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

    fun startGoogleSignIn(
        clientId: String,
        launchActivityResult: (IntentSenderRequest) -> Unit,
    ) {
        appLoginState = AppLoginState.Loading
        Identity.getSignInClient(application)
            .beginSignIn(
                BeginSignInRequest.Builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.Builder()
                            .setSupported(true)
                            .setServerClientId(clientId)
                            .setFilterByAuthorizedAccounts(false)
                            .build(),
                    )
                    .build(),
            )
            .addOnSuccessListener { result ->
                launchActivityResult(
                    IntentSenderRequest.Builder(result.pendingIntent.intentSender).build(),
                )
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Sign-in failed because:", e)
                appLoginState = AppLoginState.Error // TODO error message
            }
    }

    fun handleGoogleSignInResult(
        result: ActivityResult,
    ) {
        when (result.resultCode) {
            ComponentActivity.RESULT_OK -> {
                try {
                    val signInCredentials = Identity.getSignInClient(application)
                        .getSignInCredentialFromIntent(result.data)

                    // https://learn.microsoft.com/en-us/azure/app-service/configure-authentication-customize-sign-in-out#client-directed-sign-in
                    // need an authorization code to send with the request
                    // some examples online have used an access token for the authorization code

                    val idToken = signInCredentials.googleIdToken ?: run {
                        appLoginState = AppLoginState.Error // TODO error message
                        return@handleGoogleSignInResult
                    }

                    Log.d(TAG, idToken)

                    viewModelScope.launch {
                        appLoginState = AppLoginState.Loading
                        appLoginState = try {
                            val user = repo.getFromIdToken(idToken)
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

    fun signOut() {
        Identity.getSignInClient(application).signOut()
        appLoginState = AppLoginState.Unused
    }
}
