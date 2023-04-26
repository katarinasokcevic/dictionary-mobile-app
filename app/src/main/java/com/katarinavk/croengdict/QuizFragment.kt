package net.katarinavk.croengdict

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.katarinavk.croengdict.databinding.FragmentQuizBinding


class QuizFragment : Fragment() {
    private lateinit var binding:FragmentQuizBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        binding.quizStart.setOnClickListener {
            startQuiz(if (binding.textTranslationHrv.isChecked) "hrv" else "eng")
        }
        return binding.root
    }

    private fun startQuiz(lang: String) {
        val intent = Intent(activity, QuizActivity::class.java).apply {
            putExtra("lang", lang)
        }
        startActivity(intent)
    }
}