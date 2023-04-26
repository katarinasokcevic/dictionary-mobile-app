package net.katarinavk.croengdict

import android.content.Context

class PersistedTranslations(context: Context?, filename: String) {
    private val context: Context?
    private val filename: String

    init {
        this.context = context
        this.filename = filename
    }

    class DataEntry(val lang: String, val text1: String, val text2: String)

    fun persist(lang: String, k: String, v: String) {
        var shouldPersist = true
        this.getAll().iterator().forEach {
            if (it.lang == lang && it.text1 == k) {
                shouldPersist = false
                return
            }
        }
        if (!shouldPersist) {
            return
        }
        context?.openFileOutput(filename, Context.MODE_APPEND)?.use {
            it.write(lang.toByteArray())
            it.write('\t'.code)
            it.write(k.toByteArray())
            it.write('\t'.code)
            it.write(v.toByteArray())
            it.write('\n'.code)
        }
    }

    fun getAll(): ArrayList<DataEntry> {
        val out = ArrayList<DataEntry>()
        if (context != null && context.fileList().contains(filename)) {
            context.openFileInput(filename).bufferedReader().useLines {
                it.forEach {
                    val line = it.toString().split("\t", limit = 3)
                    if (line.size == 3) {
                        out.add(DataEntry(line[0], line[1], line[2]))
                    }
                }
            }
        }
        return out
    }

    fun deleteAll(): Boolean {
        return context?.deleteFile(filename) ?: false
    }

    fun deleteOne(lang: String, k: String) {
        val all = this.getAll()
        this.deleteAll()
        all.iterator().forEach {
            if (!it.lang.contentEquals(lang) || !it.text1.contentEquals(k)) {
                this.persist(it.lang, it.text1, it.text2)
            }
        }
    }
}