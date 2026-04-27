package app.krafted.pharaohsjourney.ui

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object ChamberIntro : Screen("chamber_intro/{chamberId}") {
        fun createRoute(chamberId: Int) = "chamber_intro/$chamberId"
    }
    object Question : Screen("question/{chamberId}") {
        fun createRoute(chamberId: Int) = "question/$chamberId"
    }
    object ChamberComplete : Screen("chamber_complete/{chamberId}") {
        fun createRoute(chamberId: Int) = "chamber_complete/$chamberId"
    }
    object GameOver : Screen("game_over/{chamberId}") {
        fun createRoute(chamberId: Int) = "game_over/$chamberId"
    }
    object Trap : Screen("trap/{chamberId}") {
        fun createRoute(chamberId: Int) = "trap/$chamberId"
    }
    object Victory : Screen("victory")
    object Leaderboard : Screen("leaderboard")
}
