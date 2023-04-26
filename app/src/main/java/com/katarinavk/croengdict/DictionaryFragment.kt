package net.katarinavk.croengdict

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import net.katarinavk.croengdict.databinding.FragmentDictionaryBinding
import java.util.*
import kotlin.collections.ArrayList

const val MAX_AUTOCOMPLETE = 10

class DictionaryFragment : Fragment() {
    private lateinit var binding: FragmentDictionaryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        val dictionary = (activity as MainActivity).dictionary ?: return binding.root
        val ti = binding.translateInput

        val savedPT = PersistedTranslations(context, "saved.txt")
        val historyPT = PersistedTranslations(context, "history.txt")

        binding.clear.setOnClickListener {
            ti.text?.clear()
            binding.translateOutput.text = ""
        }

        binding.reverse.setOnClickListener {
            val txt1 = binding.lang1.text
            val txt2 = binding.lang2.text
            binding.lang1.text = txt2
            binding.lang2.text = txt1
        }

        val acData = ArrayList<String>()
        val acAdapter = AutocompleteAdapter(ti.context, android.R.layout.simple_dropdown_item_1line, acData)
        ti.setAdapter(acAdapter)
        ti.doOnTextChanged { text, _, _, _ ->
            acData.clear()
            var n = 0
            val l = if (binding.lang1.text.toString() == "HRV") dictionary.hrList else dictionary.enList
            for (it in l) {
                if (it.lowercase().startsWith(ti.text.toString().lowercase())) {
                    acData.add(it)
                    if (n++ >= MAX_AUTOCOMPLETE) {
                        break
                    }
                }
            }
            if (n < MAX_AUTOCOMPLETE) {
                n = MAX_AUTOCOMPLETE - n
                val ld = Levenshtein()
                data class Tuple(val data: String, var distance: Int)
                val pq = PriorityQueue<Tuple>() { t1: Tuple, t2: Tuple ->
                    t2.distance - t1.distance
                }
                for (it in l) {
                    val distance = ld.compute(ti.text.toString().lowercase(), it.lowercase())
                    if (distance > 20 ) {
                        continue
                    }
                    Log.i("sokcevic", it.lowercase() + " " + distance)
                    if (pq.size == n && distance >= pq.peek()!!.distance) {
                        continue
                    }
                    if (pq.size == n) {
                        pq.poll()
                    }
                    pq.add(Tuple(it, distance))
                }
                val elements = arrayOfNulls<String>(pq.size)
                while (pq.size > 0) {
                    elements[pq.size - 1] = pq.poll()!!.data
                }
                // Trigger autocorrect
                acData.addAll(elements.requireNoNulls())
            }
            acAdapter.notifyDataSetChanged()
        }

        ti.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                ti.dismissDropDown()
                binding.translate.performClick()
            }
            false
        }
        ti.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ -> binding.translate.performClick() }

        binding.translate.setOnClickListener { it ->
            // Sakrij tipkovnicu
            val act = (activity as MainActivity)
            val imm = act.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(it.windowToken, 0)

            // Prevedi
            val translate = dictionary.translate(
                ti.text.toString().trim(),
                binding.lang1.text.toString()
            )
            binding.translateOutput.text = translate.joinToString(separator = ", ", limit = 5)
            if (translate.size > 0) {
                historyPT.persist(
                    binding.lang1.text.toString(),
                    ti.text.toString().trim(),
                    binding.translateOutput.text.toString()
                )
            }
        }

        binding.copy.setOnClickListener {
            val clipboard: ClipboardManager? =
                it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            if (clipboard != null) {
                val translated = binding.translateOutput.text.toString()
                val clip = ClipData.newPlainText(translated, translated)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(it.context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }
        }

        binding.save.setOnClickListener {
            if (!binding.translateInput.text.isEmpty() && !binding.translateOutput.text.isEmpty()) {
                savedPT.persist(
                    binding.lang1.text.toString(),
                    ti.text.toString(),
                    binding.translateOutput.text.toString()
                )
                Toast.makeText(it.context, "Bookmarked", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}
