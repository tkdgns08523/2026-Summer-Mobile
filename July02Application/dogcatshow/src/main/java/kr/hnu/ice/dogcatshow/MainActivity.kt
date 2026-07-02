package kr.hnu.ice.dogcatshow

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.dogcatshow.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.Dogbtn.setOnClickListener {
            binding.Dogbtn.visibility = View.VISIBLE
            binding.Catbtn.visibility = View.INVISIBLE
        }

        binding.Catbtn.setOnClickListener {
            binding.Dogbtn.visibility = View.INVISIBLE
            binding.Catbtn.visibility = View.VISIBLE
        }
    }
}