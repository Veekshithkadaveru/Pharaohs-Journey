package app.krafted.pharaohsjourney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.krafted.pharaohsjourney.ui.Screen
import app.krafted.pharaohsjourney.ui.theme.PharaohsJourneyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PharaohsJourneyTheme {
                PharaohsJourneyNavHost()
            }
        }
    }
}

@Composable
private fun PharaohsJourneyNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Splash.route) {
            PlaceholderScreen("Splash")
        }
        composable(Screen.Home.route) {
            PlaceholderScreen("Home — Tomb Map")
        }
        composable(
            route = Screen.ChamberIntro.route,
            arguments = listOf(navArgument("chamberId") { type = NavType.IntType })
        ) {
            PlaceholderScreen("Chamber Intro")
        }
        composable(
            route = Screen.Question.route,
            arguments = listOf(navArgument("chamberId") { type = NavType.IntType })
        ) {
            PlaceholderScreen("Question Screen")
        }
        composable(
            route = Screen.ChamberComplete.route,
            arguments = listOf(navArgument("chamberId") { type = NavType.IntType })
        ) {
            PlaceholderScreen("Chamber Complete")
        }
        composable(
            route = Screen.GameOver.route,
            arguments = listOf(navArgument("chamberId") { type = NavType.IntType })
        ) {
            PlaceholderScreen("Game Over")
        }
        composable(Screen.Victory.route) {
            PlaceholderScreen("Victory")
        }
        composable(Screen.Leaderboard.route) {
            PlaceholderScreen("Leaderboard")
        }
    }
}

@Composable
private fun PlaceholderScreen(label: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = label)
    }
}
