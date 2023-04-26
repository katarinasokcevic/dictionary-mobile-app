package net.katarinavk.croengdict

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class Dictionary(f: InputStream) {
    private var dictEN = HashMap<String, ArrayList<String>>()
    private var dictHR = HashMap<String, ArrayList<String>>()
    var enList = ArrayList<String>()
    var hrList = ArrayList<String>()

    init {
        val reader = BufferedReader(InputStreamReader(f))
        reader.forEachLine {
            val s = it.split('\t')
            if (s.size == 2) {
                if (!dictEN.contains(s[0])) {
                    dictEN[s[0]] = ArrayList();
                    enList.add(s[0])
                }
                dictEN[s[0]]!!.add(s[1])

                if (!dictHR.contains(s[1])) {
                    dictHR[s[1]] = ArrayList();
                    hrList.add(s[1])
                }
                dictHR[s[1]]!!.add(s[0])
            }
        }
    }

    fun wordOfTheDay(): String {
        val currentDate = SimpleDateFormat("dd.M.yyyy.", Locale.US).format(Date())
        val md5 = MessageDigest.getInstance("MD5").digest(currentDate.encodeToByteArray())
        var n = 0
        md5.forEach {
            n = n * 256 + it.toUByte().toInt()
            if (n > enList.size) {
                n %= enList.size
            }
        }

        return enList[n]
    }

    fun translate(s: String, lang: String): ArrayList<String> {
        if (lang.lowercase() == "hrv") {
            return dictHR[s] ?: ArrayList()
        }
        return dictEN[s] ?: ArrayList()
    }
}