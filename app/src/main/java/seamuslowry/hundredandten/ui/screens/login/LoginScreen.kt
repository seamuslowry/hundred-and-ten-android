package seamuslowry.hundredandten.ui.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import seamuslowry.hundredandten.R

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val appLoginState = viewModel.appLoginState

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = viewModel::handleGoogleSignInResult,
    )

    val clientId = stringResource(id = R.string.client_id)

    Column {
        Button(
            onClick = { viewModel.startGoogleSignIn(clientId, launcher::launch) },
            enabled = viewModel.appLoginState !is AppLoginState.Loading,
            modifier = modifier,
        ) {
            Text(text = stringResource(id = R.string.app_name))
        }
        Button(
            onClick = { viewModel.signOut() },
            enabled = viewModel.appLoginState is AppLoginState.Success,
            modifier = modifier,
        ) {
            Text(text = stringResource(id = R.string.client_id))
        }
    }
    when (appLoginState) {
        is AppLoginState.Success -> Text(text = appLoginState.user.id)
        else -> {}
    }
}
