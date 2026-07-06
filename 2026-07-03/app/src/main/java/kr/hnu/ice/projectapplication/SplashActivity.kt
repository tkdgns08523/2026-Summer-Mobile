package kr.hnu.ice.projectapplication

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.hnu.ice.projectapplication.database.AppDatabase
import kr.hnu.ice.projectapplication.util.PreferenceManager

class SplashActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private val prefs by lazy { PreferenceManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(SPLASH_DELAY_MS)
            val next = when {
                prefs.isOnboarded -> MainActivity::class.java
                db.userDao().getAll().isNotEmpty() -> LoginActivity::class.java
                else -> OnboardingActivity::class.java
            }
            startActivity(Intent(this@SplashActivity, next))
            finish()
        }
    }

    companion object {
        private const val SPLASH_DELAY_MS = 1200L
    }
}
