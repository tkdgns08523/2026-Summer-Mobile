package kr.hnu.ice.fragmentapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.fragmentapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.firstbtn.isEnabled = false

        binding.firstbtn.setOnClickListener {
            binding.firstbtn.isEnabled = false
            binding.secondbtn.isEnabled = true
            binding.thirdbtn.isEnabled = true
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FirstFragment())
                .commit()
        }

        binding.secondbtn.setOnClickListener {
            binding.firstbtn.isEnabled = true
            binding.secondbtn.isEnabled = false
            binding.thirdbtn.isEnabled = true
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, SecondFragment())
                .commit()
        }

        binding.thirdbtn.setOnClickListener {
            binding.thirdbtn.isEnabled = false
            binding.firstbtn.isEnabled = true
            binding.secondbtn.isEnabled = true
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ThirdFragment())
                .commit()
        }
    }
}