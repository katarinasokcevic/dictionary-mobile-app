package net.katarinavk.croengdict

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val pt = PersistedTranslations(this, "history.txt")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        title = "History"
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerAdapter(pt)

        findViewById<Button>(R.id.clear_history).setOnClickListener {
            pt.deleteAll()
            this.finish()
        }
    }
}