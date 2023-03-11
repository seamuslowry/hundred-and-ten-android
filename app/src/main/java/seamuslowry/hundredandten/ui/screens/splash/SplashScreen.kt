package seamuslowry.hundredandten.ui.screens.splash

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SplashScreen(
    onComplete: (data: SplashData) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val state = viewModel.state

    LaunchedEffect(key1 = state) {
        if (state is SplashState.Complete) {
            onComplete(state.data)
        }
    }

    when (state) {
        is SplashState.Loading -> CircularProgressIndicator(modifier = modifier)
        else -> {}
    }
}
