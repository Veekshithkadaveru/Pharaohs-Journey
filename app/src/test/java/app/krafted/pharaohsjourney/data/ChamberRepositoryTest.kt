package app.krafted.pharaohsjourney.data

import app.krafted.pharaohsjourney.data.db.JourneyConverters
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChamberRepositoryTest {
    @Test
    fun parseChambers_loadsSevenChambersWithFiveQuestionsEach() {
        val json = File("src/main/assets/chambers.json").readText()

        val chambers = parseChambers(json)

        assertEquals(7, chambers.size)
        assertEquals((1..7).toList(), chambers.map { it.id })
        assertTrue(chambers.all { it.questions.size == 5 })
    }

    @Test(expected = IllegalArgumentException::class)
    fun parseChambers_rejectsInvalidCorrectAnswer() {
        val json = """
            {
              "chambers": [
                ${validChamberJson(1, "bad_answer", "Missing")},
                ${validChamberJson(2)}, ${validChamberJson(3)}, ${validChamberJson(4)},
                ${validChamberJson(5)}, ${validChamberJson(6)}, ${validChamberJson(7)}
              ]
            }
        """.trimIndent()

        parseChambers(json)
    }

    @Test
    fun journeyConverters_roundTripCompletedChamberIds() {
        val converters = JourneyConverters()

        val stored = converters.fromCompletedChamberIds(listOf(3, 1, 3, 2))
        val restored = converters.toCompletedChamberIds(stored)

        assertEquals(listOf(1, 2, 3), restored)
    }

    private fun validChamberJson(
        chamberId: Int,
        firstQuestionId: String = "c${chamberId}_q1",
        firstCorrectAnswer: String = "A"
    ): String {
        val questions = (1..5).joinToString(",") { questionNumber ->
            val questionId = if (questionNumber == 1) firstQuestionId else "c${chamberId}_q$questionNumber"
            val correctAnswer = if (questionNumber == 1) firstCorrectAnswer else "A"
            """
                {
                  "id": "$questionId",
                  "type": "MULTIPLE_CHOICE",
                  "text": "Question $questionNumber",
                  "options": ["A", "B"],
                  "correctAnswer": "$correctAnswer",
                  "correctReaction": "Correct",
                  "wrongReaction": "Wrong",
                  "trapText": "Trap"
                }
            """.trimIndent()
        }
        return """
            {
              "id": $chamberId,
              "name": "Chamber $chamberId",
              "guardian": "Guardian $chamberId",
              "accentColor": "#FFFFFF",
              "backgroundDrawable": "egpt_back_1",
              "symbolDrawable": "egpt_sym_1",
              "introText": "Intro",
              "completeText": "Complete",
              "questions": [$questions]
            }
        """.trimIndent()
    }
}
