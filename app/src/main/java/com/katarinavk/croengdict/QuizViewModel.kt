package net.katarinavk.croengdict

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class QuizViewModel: ViewModel() {
    private var wordList: ArrayList<String> = ArrayList()
    private var correctAnswer: String = ""
    private var random: Random = Random(Calendar.getInstance().timeInMillis)
    private var dictionary: Dictionary? = null
    private var lang: String = ""

    private val _quizState = QuizState()
    val quizState = MutableLiveData(_quizState)

    fun start(dictionary: Dictionary, lang: String) {
        this.dictionary = dictionary
        this.lang = lang
        wordList = if (lang == "hrv") dictionary.hrList  else dictionary.enList
        generateNewQuestion()
        quizState.postValue(_quizState.copy())
    }

    fun answer(answer: String) {
        if (answer == correctAnswer) {
            _quizState.score++;
        } else {
            _quizState.heartRemaining--;
        }
        if (_quizState.heartRemaining > 0) {
            generateNewQuestion()
        }
        quizState.postValue(_quizState.copy())
    }

    private fun generateNewQuestion() {
        val question = wordList.random(random)
        _quizState.question = question

        correctAnswer = dictionary!!.translate(question, lang)[0]
        val offeredAnswers = arrayOf(correctAnswer, generateWrongAnswer(), generateWrongAnswer(), generateWrongAnswer())
        offeredAnswers.shuffle()
        _quizState.answers = offeredAnswers
    }

    private fun generateWrongAnswer(): String {
        var wrongWord: String
        do {
            wrongWord = dictionary!!.translate(wordList.random(random), lang)[0]
        } while (wrongWord == correctAnswer)
        return wrongWord
    }
}
