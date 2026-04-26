package app.krafted.pharaohsjourney.data.model

data class JourneyQuestion(
    val id: String,
    val type: QuestionType,
    val text: String,
    val options: List<String>,
    val correctAnswer: String,
    val correctReaction: String,
    val wrongReaction: String,
    val trapText: String
)
