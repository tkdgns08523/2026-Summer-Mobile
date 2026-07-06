package kr.hnu.ice.projectapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.hnu.ice.projectapplication.R
import kr.hnu.ice.projectapplication.model.User

class AccountAdapter(
    private val accounts: List<User>,
    private val onAccountClick: (User) -> Unit
) : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(accounts[position], onAccountClick)
    }

    override fun getItemCount(): Int = accounts.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNickname: TextView = itemView.findViewById(R.id.tvAccountNickname)

        fun bind(user: User, onAccountClick: (User) -> Unit) {
            tvNickname.text = user.nickname
            itemView.setOnClickListener { onAccountClick(user) }
        }
    }
}
