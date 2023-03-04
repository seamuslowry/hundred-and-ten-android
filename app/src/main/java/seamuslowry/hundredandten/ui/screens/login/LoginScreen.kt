package seamuslowry.hundredandten.ui.screens.login

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import seamuslowry.hundredandten.R

private const val TAG = "LoginScreen"

private fun googleSignIn(
    activity: Activity,
    options: GoogleIdTokenRequestOptions,
    launchActivityResult: (IntentSenderRequest) -> Unit,
) {
    Identity.getSignInClient(activity)
        .beginSignIn(
            BeginSignInRequest.Builder()
                .setGoogleIdTokenRequestOptions(options)
                .build(),
        )
        .addOnSuccessListener { result ->
            launchActivityResult(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Sign-in failed because:", e)
        }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
) {
    val activity = LocalContext.current as Activity
    val options = GoogleIdTokenRequestOptions.Builder()
        .setSupported(true)
        .setServerClientId(stringResource(R.string.client_id))
        .setFilterByAuthorizedAccounts(true)
        .build()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        when (result.resultCode) {
            ComponentActivity.RESULT_OK -> {
                try {
                    val signInCredentials = Identity.getSignInClient(activity)
                        .getSignInCredentialFromIntent(result.data)

                    Log.d(TAG, "Will eventually use: ${signInCredentials.googleIdToken}")
                    // hit the .auth/login/google endpoint for auth code
                    // put in X-ZUMO-AUTH header to authorize
                    // call endpoint to set / update user information
                } catch (e: ApiException) {
                    Log.e(TAG, "Sign-in failed with error code:", e)
                }
            }
            else -> Log.e(TAG, "Sign-in failed")
        }
    }

    Button(onClick = { googleSignIn(activity, options, launcher::launch) }, modifier = modifier) {
        Text(text = stringResource(id = R.string.app_name))
    }
}
