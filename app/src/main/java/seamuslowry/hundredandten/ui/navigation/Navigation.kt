package seamuslowry.hundredandten.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import seamuslowry.hundredandten.ui.screens.login.LoginScreen
import seamuslowry.hundredandten.ui.screens.settings.SettingsScreen

sealed class Screen<DataType>(protected val identifier: String, private val defaultData: DataType?) {
    object Login : Screen<Boolean>("login", false) {
        const val autoSelect = "autoSelect"
        override fun route(data: Boolean?) = "$identifier?$autoSelect=${data ?: "{$autoSelect}"}"
    }
    object Settings : Screen<Unit>("settings", Unit)

    open fun route(data: DataType? = defaultData) = identifier
}

@Composable
fun Navigation(
    startDestination: String,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
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
                onComplete = {
                    navController.navigate(Screen.Settings.route()) {
                        popUpTo(Screen.Login.route()) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.Settings.route()) {
            SettingsScreen(onLogout = {
                navController.navigate(Screen.Login.route(false)) {
                    popUpTo(Screen.Settings.route()) { inclusive = true }
                }
            })
        }
    }
}
