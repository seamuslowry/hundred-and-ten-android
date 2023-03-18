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
import com.auth0.android.jwt.JWT
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import seamuslowry.hundredandten.BuildConfig
import seamuslowry.hundredandten.data.AuthRepository
import seamuslowry.hundredandten.data.UserRepository
import javax.inject.Inject

private const val TAG = "LoginViewModel"

sealed interface AppLoginState {
    object Success : AppLoginState
    object Loading : AppLoginState // TODO loading may need additional indicator to show work is happening after google sign in
    object Error : AppLoginState
    object LoggedOut : AppLoginState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: UserRepository,
    private val auth: AuthRepository,
    private val application: Application,
) : ViewModel() {
    var state: AppLoginState by mutableStateOf(AppLoginState.LoggedOut)
        private set

    private val client by lazy {
        Identity.getSignInClient(application)
    }

    fun startGoogleSignIn(
        launchActivityResult: (IntentSenderRequest) -> Unit,
        autoSelect: Boolean = false,
    ) {
        state = AppLoginState.Loading

        client
            .beginSignIn(
                BeginSignInRequest.Builder()
                    .setAutoSelectEnabled(autoSelect)
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.Builder()
                            .setSupported(true)
                            .setServerClientId(BuildConfig.GOOGLE_SIGN_IN_SERVER_CLIENT_ID)
                            .setFilterByAuthorizedAccounts(autoSelect)
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
                state = AppLoginState.Error
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

                    val idToken = signInCredentials.googleIdToken ?: run {
                        // TODO use ID token to get user picture url
                        state = AppLoginState.Error // TODO error message
                        return@handleGoogleSignInResult
                    }

                    val idJwt = JWT(idToken)
                    val name = idJwt.getClaim("name").asString() ?: ""
                    val pictureUrl = idJwt.getClaim("picture").asString() ?: ""

                    viewModelScope.launch {
                        state = try {
                            val response = repo.loginWithGoogle(idToken)
                            auth.saveToken(response.authenticationToken)
                            repo.login(name, pictureUrl) // TODO verify this works
                            AppLoginState.Success
                        } catch (e: Exception) {
                            AppLoginState.Error
                        }
                    }
                } catch (e: ApiException) {
                    Log.e(TAG, "Sign-in failed with error code:", e)
                    state = AppLoginState.Error
                }
            }
            else -> {
                Log.e(TAG, "Sign-in failed")
                state = AppLoginState.Error
            }
        }
    }
}
