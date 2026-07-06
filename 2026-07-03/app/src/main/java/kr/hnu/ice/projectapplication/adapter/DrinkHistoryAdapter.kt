package kr.hnu.ice.projectapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.hnu.ice.projectapplication.R
import kr.hnu.ice.projectapplication.model.DrinkRecord
import kr.hnu.ice.projectapplication.util.DateUtil

class DrinkHistoryAdapter(
    private val onDeleteClick: (DrinkRecord) -> Unit
) : ListAdapter<DrinkRecord, DrinkHistoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drink_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onDeleteClick)
    }

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val tvAmount: TextView = itemView.findViewById(R.id.tvItemAmount)
        private val tvTime: TextView = itemView.findViewById(R.id.tvItemTime)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(record: DrinkRecord, onDeleteClick: (DrinkRecord) -> Unit) {
            tvAmount.text = itemView.context.getString(R.string.history_item_format, record.amount)
            tvTime.text = DateUtil.toTime(record.timestamp)
            btnDelete.setOnClickListener { onDeleteClick(record) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DrinkRecord>() {
            override fun areItemsTheSame(oldItem: DrinkRecord, newItem: DrinkRecord) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DrinkRecord, newItem: DrinkRecord) =
                oldItem == newItem
        }
    }
}
