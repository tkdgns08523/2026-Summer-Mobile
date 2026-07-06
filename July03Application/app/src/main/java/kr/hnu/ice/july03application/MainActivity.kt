package kr.hnu.ice.july03application

import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.july03application.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var elapsedTime = 0L
    var initTime = System.currentTimeMillis()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startbtn.setEnabled(true)
        binding.stopbtn.setEnabled(false)

        binding.startbtn.setOnClickListener {
            binding.chronometer.setBase(SystemClock.elapsedRealtime() + elapsedTime)
            binding.startbtn.isEnabled = false
            binding.stopbtn.isEnabled = true
            binding.chronometer.start()

            Toast.makeText(this@MainActivity, "시간 측정이 시작되었습니다.", Toast.LENGTH_SHORT).show()
        }
        binding.stopbtn.setOnClickListener {
            elapsedTime = binding.chronometer.base - SystemClock.elapsedRealtime()
            binding.stopbtn.isEnabled = false
            binding.startbtn.isEnabled = true
            binding.chronometer.stop()
            Toast.makeText(this@MainActivity, "시간 측정이 종료되었습니다.", Toast.LENGTH_SHORT).show()
        }
        binding.resetbtn.setOnClickListener {
            elapsedTime = 0L
            binding.chronometer.base = SystemClock.elapsedRealtime()
            binding.chronometer.stop()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - initTime > 2000) {
                    Toast.makeText(this@MainActivity, "2초 이후에 종료할 수 있습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    finish()
                }
            }
        })
    }
}