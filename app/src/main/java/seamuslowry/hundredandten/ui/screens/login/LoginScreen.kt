package seamuslowry.hundredandten.ui.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import seamuslowry.hundredandten.R

@Composable
fun LoginScreen(
    autoSelect: Boolean,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state = viewModel.state

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = viewModel::handleGoogleSignInResult,
    )

    LaunchedEffect(key1 = state) {
        if (state is LoginState.LoggedOut) {
            viewModel.startGoogleSignIn(launcher::launch)
        }
        if (state is LoginState.Success) {
            onComplete()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (state) {
                is LoginState.Error -> {
                    Button(
                        onClick = { viewModel.startGoogleSignIn(launcher::launch, autoSelect) },
                        enabled = viewModel.state !is LoginState.Loading && viewModel.state !is LoginState.Success,
                    ) {
                        Text(text = stringResource(id = R.string.retry))
                    }
                    Text(text = stringResource(R.string.sign_in_failed))
                }
                is LoginState.Loading -> {
                    CircularProgressIndicator()
                    AnimatedVisibility(visible = state.step == LoadingStep.GAME_API) {
                        Text(text = stringResource(R.string.creating_account))
                    }
                }
                else -> {}
            }
        }
    }
}
