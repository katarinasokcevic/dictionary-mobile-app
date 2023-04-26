package net.katarinavk.croengdict

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(pt: PersistedTranslations) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private val pt: PersistedTranslations
    private var data: ArrayList<PersistedTranslations.DataEntry>
    private var self: RecyclerAdapter

    init {
        this.pt = pt
        this.data = pt.getAll()
        this.self = this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textForTranslation.text = "[" + data[position].lang + "] " + data[position].text1
        holder.textTranslation.text = data[position].text2
        holder.button.tag = data[position].lang + "\t" + data[position].text1
    }

    override fun getItemCount(): Int {
        return data.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textForTranslation: TextView
        var textTranslation: TextView
        var button: Button

        init {
            textForTranslation = itemView.findViewById(R.id.textForTranslation)
            textTranslation = itemView.findViewById(R.id.textTranslation)
            button = itemView.findViewById(R.id.delete_one_item)
            button.setOnClickListener {
                val tagData = button.tag.toString().split('\t', limit = 2)
                if (tagData.size == 2) {
                    pt.deleteOne(tagData[0], tagData[1])
                    data = pt.getAll()
                    self.notifyDataSetChanged()
                }
            }
        }

    }


}