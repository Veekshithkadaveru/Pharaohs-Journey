package app.krafted.pharaohsjourney.data

import android.content.Context
import app.krafted.pharaohsjourney.data.model.Chamber
import com.google.gson.Gson

class ChamberRepository(
    private val context: Context,
    private val gson: Gson = Gson()
) {
    private var cachedChambers: List<Chamber>? = null

    fun getChambers(): List<Chamber> {
        cachedChambers?.let { return it }

        val json = context.assets.open(CHAMBERS_FILE).bufferedReader().use { it.readText() }
        return parseChambers(json, gson).also { cachedChambers = it }
    }

    fun getChamber(chamberId: Int): Chamber? = getChambers().firstOrNull { it.id == chamberId }

    companion object {
        private const val CHAMBERS_FILE = "chambers.json"
    }
}

internal fun parseChambers(json: String, gson: Gson = Gson()): List<Chamber> {
    val root = gson.fromJson(json, ChamberRoot::class.java)
        ?: throw IllegalStateException("Unable to parse chambers.json")
    val chambers = root.chambers.sortedBy { it.id }
    validateChambers(chambers)
    return chambers
}

private fun validateChambers(chambers: List<Chamber>) {
    require(chambers.size == 7) { "Expected 7 chambers, found ${chambers.size}" }
    require(chambers.map { it.id }
        .distinct().size == chambers.size) { "Chamber IDs must be unique" }

    val questionIds = mutableSetOf<String>()
    chambers.forEach { chamber ->
        require(chamber.questions.size == 5) {
            "Chamber ${chamber.id} must contain exactly 5 questions"
        }
        chamber.questions.forEach { question ->
            require(questionIds.add(question.id)) { "Question ID ${question.id} is duplicated" }
            require(question.correctAnswer in question.options) {
                "Question ${question.id} correct answer must be one of its options"
            }
        }
    }
}

private data class ChamberRoot(
    val chambers: List<Chamber>
)
