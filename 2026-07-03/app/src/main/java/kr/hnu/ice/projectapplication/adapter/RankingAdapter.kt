package kr.hnu.ice.projectapplication.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kr.hnu.ice.projectapplication.R
import kr.hnu.ice.projectapplication.model.Friend

class RankingAdapter(private val items: List<Friend>) :
    RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ranking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position + 1, items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card = itemView as MaterialCardView
        private val tvRank: TextView = itemView.findViewById(R.id.tvRank)
        private val tvEmoji: TextView = itemView.findViewById(R.id.tvRankPetEmoji)
        private val tvNickname: TextView = itemView.findViewById(R.id.tvRankNickname)
        private val tvRate: TextView = itemView.findViewById(R.id.tvRankRate)

        fun bind(rank: Int, friend: Friend) {
            tvRank.text = rank.toString()
            tvEmoji.text = friend.petEmoji
            tvNickname.text = friend.nickname
            tvRate.text = itemView.context.getString(R.string.ranking_rate_format, friend.achievementRate)
            card.setCardBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    if (friend.isMe) R.color.water_bg else R.color.surface_card
                )
            )

            val medalColorRes = when (rank) {
                1 -> R.color.gold
                2 -> R.color.silver
                3 -> R.color.bronze
                else -> null
            }
            val badge = tvRank.parent as? FrameLayout
            val badgeBg = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(
                    ContextCompat.getColor(
                        itemView.context,
                        medalColorRes ?: R.color.water_bg
                    )
                )
            }
            badge?.background = badgeBg
            tvRank.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    if (medalColorRes != null) R.color.white else R.color.water_blue_dark
                )
            )
        }
    }
}
