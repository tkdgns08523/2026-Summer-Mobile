package kr.hnu.ice.recyclerview

class MyAdapter(val datas: List<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: kr.hnu.ice.recyclerview.databinding.ItemMainBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): MyViewHolder {
        val binding = kr.hnu.ice.recyclerview.databinding.ItemMainBinding.inflate(android.view.LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.itemData.text = datas[position]
        holder.binding.itemRoot.setOnClickListener {
            android.widget.Toast.makeText(holder.itemView.context, "Clicked: ${datas[position]}", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

}