package seamuslowry.hundredandten

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import seamuslowry.hundredandten.ui.theme.HundredAndTenTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private val tokenRequestOptions by lazy {
        BeginSignInRequest.GoogleIdTokenRequestOptions.Builder()
            .setSupported(true)
            .setServerClientId(getString(R.string.client_id))
            .setFilterByAuthorizedAccounts(true)
            .build()
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                try {
                    val signInCredentials = Identity.getSignInClient(this)
                        .getSignInCredentialFromIntent(result.data)

                    Log.d(TAG, "Will eventually use: ${signInCredentials.googleIdToken}")
                    // hit the .auth/login/google endpoint for auth code
                    // put in X-ZUMO-AUTH header to authorize
                    // need to figure out how to include identity information
                } catch (e: ApiException) {
                    Log.e(TAG, "Sign-in failed with error code:", e)
                }
            }
            else -> Log.e(TAG, "Sign-in failed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HundredAndTenTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Greeting("Android")
                }
            }
        }

        Identity.getSignInClient(this)
            .beginSignIn(
                BeginSignInRequest.Builder()
                    .setGoogleIdTokenRequestOptions(tokenRequestOptions)
                    .build(),
            )
            .addOnSuccessListener { result ->
                activityResultLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Sign-in failed because:", e)
            }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HundredAndTenTheme {
        Greeting("Android")
    }
}
