package kr.hnu.ice.projectapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kr.hnu.ice.projectapplication.database.AppDatabase
import kr.hnu.ice.projectapplication.model.DrinkRecord
import kr.hnu.ice.projectapplication.util.DateUtil
import kr.hnu.ice.projectapplication.util.ItemCatalog
import kr.hnu.ice.projectapplication.util.PreferenceManager

class MainActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private val prefs by lazy { PreferenceManager(this) }

    private lateinit var tvGreeting: TextView
    private lateinit var tvPet: TextView
    private lateinit var tvPetHat: TextView
    private lateinit var tvPetAccessory: TextView
    private lateinit var tvPercent: TextView
    private lateinit var tvAmount: TextView
    private lateinit var tvGoalHint: TextView
    private lateinit var progress: CircularProgressIndicator
    private lateinit var drinkButtons: List<MaterialButton>

    private var dailyGoal: Int = 2000
    private val userId get() = prefs.activeUserId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!prefs.isOnboarded) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        bindViews()
        setupButtons()
        setupBottomNav()
        findViewById<android.widget.FrameLayout>(R.id.progressContainer).setOnClickListener {
            startActivity(Intent(this, DrinkHistoryActivity::class.java))
        }
        tvPet.setOnClickListener {
            startActivity(Intent(this, CharacterActivity::class.java))
        }
        observeState()
        observeEquippedItems()
    }

    private fun bindViews() {
        tvGreeting = findViewById(R.id.tvGreeting)
        tvPet = findViewById(R.id.tvPet)
        tvPetHat = findViewById(R.id.tvPetHat)
        tvPetAccessory = findViewById(R.id.tvPetAccessory)
        tvPercent = findViewById(R.id.tvPercent)
        tvAmount = findViewById(R.id.tvAmount)
        tvGoalHint = findViewById(R.id.tvGoalHint)
        progress = findViewById(R.id.progressWater)
    }

    /** 캐릭터 화면에서 장착한 모자/액세서리를 홈 화면 펫에도 반영한다. */
    private fun observeEquippedItems() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                db.itemDao().observeAll(userId).collect { items ->
                    val equipped = items.filter { it.isEquipped }
                    bindEquippedEmoji(tvPetHat, equipped.firstOrNull { it.type == ItemCatalog.TYPE_HAT }?.emoji)
                    bindEquippedEmoji(tvPetAccessory, equipped.firstOrNull { it.type == ItemCatalog.TYPE_ACCESSORY }?.emoji)
                }
            }
        }
    }

    private fun bindEquippedEmoji(view: TextView, emoji: String?) {
        view.text = emoji.orEmpty()
        view.visibility = if (emoji == null) android.view.View.GONE else android.view.View.VISIBLE
    }

    private fun setupButtons() {
        val map = mapOf(
            R.id.btn100 to 100,
            R.id.btn200 to 200,
            R.id.btn300 to 300,
            R.id.btn500 to 500
        )
        drinkButtons = map.keys.map { findViewById<MaterialButton>(it) }
        map.forEach { (id, amount) ->
            findViewById<MaterialButton>(id).setOnClickListener { recordDrink(amount) }
        }
    }

    private fun setupBottomNav() {
        val nav = findViewById<BottomNavigationView>(R.id.bottomNav)
        nav.selectedItemId = R.id.nav_main
        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> true
                R.id.nav_stats -> {
                    startActivity(Intent(this, StatisticsActivity::class.java))
                    false
                }
                R.id.nav_stretch -> {
                    startActivity(Intent(this, StretchingActivity::class.java))
                    false
                }
                R.id.nav_ranking -> {
                    startActivity(Intent(this, RankingActivity::class.java))
                    false
                }
                R.id.nav_mypage -> {
                    startActivity(Intent(this, MyPageActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    /** 사용자 목표 + 오늘 섭취량을 함께 관찰하여 UI를 갱신한다. */
    private fun observeState() {
        val today = DateUtil.today()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    db.userDao().observeById(userId),
                    db.drinkRecordDao().observeTotalByDate(userId, today)
                ) { user, total -> user to total }
                    .collect { (user, total) ->
                        if (user != null) {
                            dailyGoal = user.dailyGoal
                            tvGreeting.text = getString(R.string.greeting, user.nickname)
                        }
                        updateUi(total)
                    }
            }
        }
    }

    private fun updateUi(total: Int) {
        val percent = if (dailyGoal > 0) (total * 100 / dailyGoal) else 0
        progress.setProgressCompat(percent.coerceIn(0, 100), true)
        tvPercent.text = getString(R.string.percent_format, percent)
        tvAmount.text = getString(R.string.progress_format, total, dailyGoal)
        tvPet.text = petEmoji(percent)

        val achieved = total >= dailyGoal
        drinkButtons.forEach { it.isEnabled = !achieved }
        tvGoalHint.visibility = if (achieved) android.view.View.VISIBLE else android.view.View.GONE
    }

    /** 진행률에 따라 펫 표정이 성장한다. */
    private fun petEmoji(percent: Int): String = when {
        percent >= 100 -> "🐔"
        percent >= 75 -> "🐥"
        percent >= 50 -> "🐤"
        percent >= 25 -> "🐣"
        else -> "🥚"
    }

    private fun recordDrink(amount: Int) {
        lifecycleScope.launch {
            val alreadyAchieved = db.drinkRecordDao().getTotalByDate(userId, DateUtil.today()) >= dailyGoal
            if (alreadyAchieved) return@launch

            val now = System.currentTimeMillis()
            db.drinkRecordDao().insert(
                DrinkRecord(userId = userId, amount = amount, timestamp = now, date = DateUtil.today())
            )

            // 펫 경험치 + 코인 보상 (물 10ml당 1exp)
            db.characterDao().getByUser(userId)?.let { pet ->
                var exp = pet.exp + amount / 10
                var level = pet.level
                while (exp >= level * 100) {
                    exp -= level * 100
                    level++
                }
                db.characterDao().update(pet.copy(exp = exp, level = level))
            }
            prefs.addCoins(amount / 100)

            val total = db.drinkRecordDao().getTotalByDate(userId, DateUtil.today())
            val msg = if (total >= dailyGoal) getString(R.string.goal_achieved)
            else getString(R.string.drink_recorded, amount)
            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }
}
