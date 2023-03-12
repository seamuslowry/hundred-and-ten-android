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

sealed class Screen<DataType>(protected val identifier: String, private val defaultData: DataType) {
    object Login : Screen<String>("login", "{autoSelect}") {
        const val autoSelect = "autoSelect"
        override fun route(data: String) = "$identifier?$autoSelect=$data"
    }
    object Home : Screen<Unit>("home", Unit)

    open fun route(data: DataType = defaultData) = identifier
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
                    navController.navigate(Screen.Home.route()) {
                        popUpTo(Screen.Login.route()) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.Home.route()) {
            // TODO make this sign out to test the whole flow
            Text(text = stringResource(id = R.string.home))
        }
    }
}