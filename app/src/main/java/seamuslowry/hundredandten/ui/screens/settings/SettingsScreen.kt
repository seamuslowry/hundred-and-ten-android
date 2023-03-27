package seamuslowry.hundredandten.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import seamuslowry.hundredandten.R
import seamuslowry.hundredandten.sources.models.User
import androidx.compose.material3.Button as MaterialButton

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) {
            onLogout()
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UserForm(
            user = state.user,
            onChange = viewModel::updateUser,
            onSave = { scope.launch { viewModel.saveUser() } },
            enabled = !state.loading.contains(SettingsBehavior.LOAD_USER) &&
                !state.loading.contains(SettingsBehavior.SAVE_USER),
            error = state.errors.contains(SettingsBehavior.SAVE_USER),
        )
        LogoutButton(
            onClick = { scope.launch { viewModel.signOut() } },
            enabled = !state.loading.contains(SettingsBehavior.LOAD_USER) &&
                !state.loading.contains(SettingsBehavior.LOGOUT),
            error = state.errors.contains(SettingsBehavior.LOGOUT),
        )
    }
}

@Composable
fun UserForm(
    user: User,
    modifier: Modifier = Modifier,
    onChange: (user: User) -> Unit = {},
    onSave: () -> Unit = {},
    enabled: Boolean = true,
    error: Boolean = false,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = user.name,
            onValueChange = {
                onChange(user.copy(name = it))
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.name)) },
            singleLine = true,
            enabled = enabled,
        )
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
        ) {
            Text(text = stringResource(R.string.save))
        }
        if (error) {
            Text(text = stringResource(R.string.save_fail), color = MaterialTheme.colorScheme.error)
        }
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
            Text(text = stringResource(R.string.log_out))
        }
        if (error) {
            Text(text = stringResource(R.string.log_out_error), color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    MaterialButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (!enabled) {
                LinearProgressIndicator()
            }
            content()
        }
    }
}
