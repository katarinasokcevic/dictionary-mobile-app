package net.katarinavk.croengdict

data class QuizState(
    var score: Int = 0,
    var heartRemaining: Int = 3,

    var question: String = "",
    var answers: Array<String> = arrayOf()
)