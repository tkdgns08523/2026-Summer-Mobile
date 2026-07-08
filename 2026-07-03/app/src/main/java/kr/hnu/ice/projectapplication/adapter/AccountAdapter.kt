package kr.hnu.ice.projectapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.hnu.ice.projectapplication.R
import kr.hnu.ice.projectapplication.model.User
import kr.hnu.ice.projectapplication.model.WaterCharacter
import kr.hnu.ice.projectapplication.util.PetSpecies

class AccountAdapter(
    private val accounts: List<Pair<User, WaterCharacter?>>,
    private val onAccountClick: (User) -> Unit
) : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (user, pet) = accounts[position]
        holder.bind(user, pet, onAccountClick)
    }

    override fun getItemCount(): Int = accounts.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvEmoji: TextView = itemView.findViewById(R.id.tvAccountEmoji)
        private val tvNickname: TextView = itemView.findViewById(R.id.tvAccountNickname)

        fun bind(user: User, pet: WaterCharacter?, onAccountClick: (User) -> Unit) {
            tvEmoji.text = if (pet != null) {
                PetSpecies.emojiForLevel(pet.species, pet.level)
            } else {
                PetSpecies.emojiForLevel(PetSpecies.CHICK, 1)
            }
            tvNickname.text = user.nickname
            itemView.setOnClickListener { onAccountClick(user) }
        }
    }
}
