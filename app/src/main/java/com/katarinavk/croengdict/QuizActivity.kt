package net.katarinavk.croengdict

import android.content.res.AssetManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import net.katarinavk.croengdict.databinding.ActivityQuizBinding
import java.util.*

class QuizActivity : AppCompatActivity() {
    private val model: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Quiz"

        val binding = ActivityQuizBinding.inflate(layoutInflater)
        val lang = intent.getStringExtra("lang")!!.lowercase()
        setContentView(binding.root)

        val dictionary = Dictionary(
            resources.assets.open("en_hr_quiz.txt", AssetManager.ACCESS_STREAMING)
        )

        val quizStateObserver = Observer<QuizState> {
            if (it.heartRemaining == 0) {
                Toast.makeText(binding.root.context, "Game over, score: " + it.score, Toast.LENGTH_LONG).show()
                onBackPressed()
            }
        }
        model.quizState.observe(this, quizStateObserver)
        model.start(dictionary, lang)
        binding.quiz = model
        binding.lifecycleOwner = this
    }
}