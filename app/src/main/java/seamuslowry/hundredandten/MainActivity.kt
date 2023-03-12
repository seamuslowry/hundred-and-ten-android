package seamuslowry.hundredandten

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import seamuslowry.hundredandten.ui.navigation.Navigation
import seamuslowry.hundredandten.ui.navigation.Screen
import seamuslowry.hundredandten.ui.theme.HundredAndTenTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var state: MainActivityState by mutableStateOf(MainActivityState.Loading)

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .onEach {
                        state = it
                    }.collect()
            }
        }

        splashScreen
            .setKeepOnScreenCondition {
                state is MainActivityState.Loading
            }

        setContent {
            HundredAndTenTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    when (state) {
                        is MainActivityState.LoggedIn -> Screen.Home.route()
                        is MainActivityState.NewUser -> Screen.Login.route(false.toString())
                        is MainActivityState.ReSignIn -> Screen.Login.route(true.toString())
                        else -> null
                    }?.let { Navigation(it) }
                }
            }
        }
    }
}
