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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import seamuslowry.hundredandten.BuildConfig
import seamuslowry.hundredandten.data.AuthRepository
import seamuslowry.hundredandten.data.UserRepository
import seamuslowry.hundredandten.models.User
import javax.inject.Inject

private const val TAG = "LoginViewModel"
private const val WEEK_IN_SECONDS = (7 * 12 * 60 * 60).toLong()

sealed interface AppLoginState {
    data class Success(val user: User) : AppLoginState
    object Loading : AppLoginState
    object Error : AppLoginState
    object Unused : AppLoginState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: UserRepository,
    private val auth: AuthRepository,
    private val application: Application,
) : ViewModel() {
    var state: AppLoginState by mutableStateOf(AppLoginState.Unused)
        private set

    private val client by lazy {
        Identity.getSignInClient(application)
    }

    private fun getUserId(authToken: String?): String? {
        val jwt = authToken?.let { JWT(it) }
        val expiringSoon = jwt?.isExpired(WEEK_IN_SECONDS)
        val userId = jwt?.subject

        return if (expiringSoon != false) null else userId
    }

    fun startGoogleSignIn(
        launchActivityResult: (IntentSenderRequest) -> Unit,
    ) {
        state = AppLoginState.Loading

        val token = runBlocking { auth.authToken.first() }
        val userId = getUserId(token)
        val signedIn = !userId.isNullOrBlank()

        // if we don't need to refresh the token, don't show login
        userId?.let {
            state = AppLoginState.Success(User(it, ""))
            return@startGoogleSignIn
        }

        client
            .beginSignIn(
                BeginSignInRequest.Builder()
                    .setAutoSelectEnabled(signedIn)
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.Builder()
                            .setSupported(true)
                            .setServerClientId(BuildConfig.GOOGLE_SIGN_IN_SERVER_CLIENT_ID)
                            .setFilterByAuthorizedAccounts(signedIn)
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
                state = AppLoginState.Error // TODO error message
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

                    viewModelScope.launch {
                        state = try {
                            val token = repo.getAccessToken(idToken)
                            val userId = getUserId(token) ?: throw Exception("token not readable")
                            auth.saveToken(token)
                            repo.getMe()
                            AppLoginState.Success(User(userId, ""))
                        } catch (e: Exception) {
                            AppLoginState.Error
                        }
                    }
                } catch (e: ApiException) {
                    Log.e(TAG, "Sign-in failed with error code:", e)
                    state = AppLoginState.Error // TODO error message
                }
            }
            else -> {
                Log.e(TAG, "Sign-in failed")
                state = AppLoginState.Error // TODO error message
            }
        }
    }

    fun signOut() {
        runBlocking {
            repo.logout()
            auth.clear()
        }
        client.signOut()
        state = AppLoginState.Unused
    }
}
