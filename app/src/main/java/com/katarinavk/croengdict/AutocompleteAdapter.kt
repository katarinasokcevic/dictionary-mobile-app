package net.katarinavk.croengdict

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.annotation.LayoutRes
import java.util.*

class AutocompleteAdapter<String>(context: Context, @LayoutRes private val layoutResource: Int, private val items: ArrayList<String>): ArrayAdapter<String>(context, layoutResource, items) {
    private var nameFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val result = FilterResults()
            result.values = items
            result.count = items.size
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }

    override fun getFilter(): Filter {
        return nameFilter
    }

}