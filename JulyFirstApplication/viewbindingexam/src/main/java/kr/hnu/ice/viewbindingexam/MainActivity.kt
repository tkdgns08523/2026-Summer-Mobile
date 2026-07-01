package kr.hnu.ice.viewbindingexam

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.viewbindingexam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.resetbtn.setOnClickListener {
            binding.titletexrt.text = "View Binding Example"
        }

        binding.changebtn.setOnClickListener {
            binding.titletexrt.text = "Changed Text"

        }
    }
}