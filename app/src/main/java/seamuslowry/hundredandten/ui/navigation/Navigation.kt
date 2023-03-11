package seamuslowry.hundredandten.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import seamuslowry.hundredandten.R
import seamuslowry.hundredandten.ui.screens.login.LoginScreen
import seamuslowry.hundredandten.ui.screens.splash.SplashScreen

sealed class Screen(val identifier: String) {
    object Splash : Screen("splash")
    object Login : Screen("login") {
        const val autoSelect = "autoSelect"

        fun route(arg: String = "{$autoSelect}") = "$identifier?$autoSelect=$arg"
    }
    object Home : Screen("home")
}

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Splash.identifier,
    ) {
        composable(Screen.Splash.identifier) {
            SplashScreen(onComplete = {
                if (it.needsSignIn) {
                    navController.navigate(Screen.Login.route(it.autoSelect.toString()))
                } else {
                    navController.navigate(Screen.Home.identifier)
                }
            })
        }
        composable(
            Screen.Login.route(),
            arguments = listOf(
                navArgument(Screen.Login.autoSelect) {
                    type = NavType.BoolType
                    defaultValue = false
                },
            ),
        ) {
            LoginScreen(
                autoSelect = it.arguments?.getBoolean(Screen.Login.autoSelect) ?: false,
                onComplete = { navController.navigate(Screen.Home.identifier) },
            )
        }
        composable(Screen.Home.identifier) {
            // TODO make this sign out to test the whole flow
            Text(text = stringResource(id = R.string.home))
        }
    }
}
