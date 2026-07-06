package kr.hnu.ice.projectapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.hnu.ice.projectapplication.R
import kr.hnu.ice.projectapplication.model.Stretching

class StretchingAdapter(
    private val items: List<Stretching>,
    private val onItemClick: (Stretching) -> Unit
) : RecyclerView.Adapter<StretchingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stretching, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvEmoji: TextView = itemView.findViewById(R.id.tvStretchEmoji)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvStretchTitle)
        private val tvSubtitle: TextView = itemView.findViewById(R.id.tvStretchSubtitle)

        fun bind(item: Stretching, onItemClick: (Stretching) -> Unit) {
            tvEmoji.text = item.emoji
            tvTitle.text = item.title
            tvSubtitle.text = itemView.context.getString(
                R.string.stretch_subtitle_format, item.bodyPart, item.durationSeconds
            )
            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}
