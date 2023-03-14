package seamuslowry.hundredandten.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import seamuslowry.hundredandten.R

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = state) {
        if (state is SettingsState.LoggedOut) {
            onLogout()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = { scope.launch { viewModel.signOut() } },
                enabled = state !is SettingsState.Loading && state !is SettingsState.LoggedOut,
            ) {
                if (state is SettingsState.Loading) {
                    CircularProgressIndicator()
                }
                Text(text = stringResource(R.string.log_out))
            }
            when (state) {
                is SettingsState.Error -> Text(text = stringResource(R.string.log_out_error))
                else -> {}
            }
        }
    }
}
