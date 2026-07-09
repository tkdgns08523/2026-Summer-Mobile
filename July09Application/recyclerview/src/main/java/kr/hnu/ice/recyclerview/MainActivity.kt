package kr.hnu.ice.recyclerview

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kr.hnu.ice.recyclerview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val datas = mutableListOf<String>()
        for (i in 1..100) {
            datas.add("Item $i")
        }

        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.adapter = MyAdapter(datas)

        binding.addbtn.setOnClickListener {
            val newItem = "Item ${datas.size + 1}"
            datas.add(newItem)
            binding.recyclerview.adapter?.notifyItemInserted(datas.size - 1)
            binding.recyclerview.scrollToPosition(datas.size - 1)
        }

        binding.delbtn.setOnClickListener {
            if (datas.isNotEmpty()) {
                val lastIndex = datas.size - 1
                datas.removeAt(lastIndex)
                binding.recyclerview.adapter?.notifyItemRemoved(lastIndex)
            }
        }
    }
}