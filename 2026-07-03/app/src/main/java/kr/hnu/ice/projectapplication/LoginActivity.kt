package kr.hnu.ice.projectapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import kr.hnu.ice.projectapplication.adapter.AccountAdapter
import kr.hnu.ice.projectapplication.database.AppDatabase
import kr.hnu.ice.projectapplication.util.PreferenceManager

/**
 * 이 기기에 저장된 로컬 계정(멀티 프로필) 중 하나를 골라 로그인하는 화면.
 * 실제 서버 인증이 없으므로, 계정을 탭하는 것만으로 로그인 처리한다.
 */
class LoginActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private val prefs by lazy { PreferenceManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<MaterialButton>(R.id.btnCreateAccount).setOnClickListener {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }

        loadAccounts()
    }

    private fun loadAccounts() {
        lifecycleScope.launch {
            val accounts = db.userDao().getAll()
            if (accounts.isEmpty()) {
                startActivity(Intent(this@LoginActivity, OnboardingActivity::class.java))
                finish()
                return@launch
            }

            findViewById<RecyclerView>(R.id.recyclerAccounts).apply {
                layoutManager = LinearLayoutManager(this@LoginActivity)
                adapter = AccountAdapter(accounts) { user -> login(user.id) }
            }
        }
    }

    private fun login(userId: Long) {
        prefs.activeUserId = userId
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
