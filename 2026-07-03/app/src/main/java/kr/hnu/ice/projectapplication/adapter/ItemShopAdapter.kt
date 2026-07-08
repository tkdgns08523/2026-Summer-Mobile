package kr.hnu.ice.projectapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.hnu.ice.projectapplication.R
import kr.hnu.ice.projectapplication.model.Item
import kr.hnu.ice.projectapplication.util.parseColorOrNull

class ItemShopAdapter(
    private val onItemClick: (Item) -> Unit
) : ListAdapter<Item, ItemShopAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val vColorSwatch: View = itemView.findViewById(R.id.vItemColorSwatch)
        private val tvName: TextView = itemView.findViewById(R.id.tvItemName)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvItemStatus)

        fun bind(item: Item, onItemClick: (Item) -> Unit) {
            val context = itemView.context
            parseColorOrNull(item.emoji)?.let { vColorSwatch.background.setTint(it) }
            tvName.text = item.name
            tvStatus.text = when {
                item.isEquipped -> context.getString(R.string.item_status_equipped)
                item.isOwned -> context.getString(R.string.item_status_owned)
                else -> context.getString(R.string.item_status_price_format, item.price)
            }
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
        }
    }
}
