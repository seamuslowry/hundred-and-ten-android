package seamuslowry.hundredandten.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import seamuslowry.hundredandten.R
import seamuslowry.hundredandten.ui.theme.HundredAndTenTheme

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
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        LogoutButton(
            onClick = { scope.launch { viewModel.signOut() } },
            enabled = state !is SettingsState.Loading && state !is SettingsState.LoggedOut,
            error = state is SettingsState.Error,
        )
    }
}

@Composable
fun LogoutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (!enabled) {
                CircularProgressIndicator()
            }
            Text(text = stringResource(R.string.log_out))
        }
        if (error) {
            Text(text = stringResource(R.string.log_out_error), color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LogoutPreview() {
    HundredAndTenTheme {
        LogoutButton(onClick = { /*TODO*/ }, error = true)
    }
}
