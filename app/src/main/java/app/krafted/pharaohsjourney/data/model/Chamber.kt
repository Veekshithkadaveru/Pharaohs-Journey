package app.krafted.pharaohsjourney.data.model

data class Chamber(
    val id: Int,
    val name: String,
    val guardian: String,
    val subtitle: String,
    val accentColor: String,
    val darkColor: String,
    val backgroundDrawable: String,
    val symbolDrawable: String,
    val guardianDrawable: String,
    val introText: String,
    val completeText: String,
    val questions: List<JourneyQuestion>
)
