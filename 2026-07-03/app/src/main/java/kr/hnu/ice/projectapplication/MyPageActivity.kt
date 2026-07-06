package kr.hnu.ice.projectapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import kr.hnu.ice.projectapplication.database.AppDatabase
import kr.hnu.ice.projectapplication.util.PreferenceManager
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class MyPageActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private val prefs by lazy { PreferenceManager(this) }
    private val userId get() = prefs.activeUserId

    private lateinit var etNickname: TextInputEditText
    private lateinit var etWeight: TextInputEditText
    private lateinit var etDailyGoal: TextInputEditText
    private lateinit var radioGroupActivity: RadioGroup
    private lateinit var tvTotalWater: TextView
    private lateinit var tvStreak: TextView
    private lateinit var tvPetLevelSummary: TextView
    private lateinit var tvAppVersion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        bindViews()
        setupActions()
        loadProfile()
        loadStats()
        showAppVersion()
    }

    private fun bindViews() {
        etNickname = findViewById(R.id.etNickname)
        etWeight = findViewById(R.id.etWeight)
        etDailyGoal = findViewById(R.id.etDailyGoal)
        radioGroupActivity = findViewById(R.id.radioGroupActivity)
        tvTotalWater = findViewById(R.id.tvTotalWater)
        tvStreak = findViewById(R.id.tvStreak)
        tvPetLevelSummary = findViewById(R.id.tvPetLevelSummary)
        tvAppVersion = findViewById(R.id.tvAppVersion)
    }

    private fun setupActions() {
        findViewById<MaterialButton>(R.id.btnSaveProfile).setOnClickListener { saveProfile() }
        findViewById<MaterialButton>(R.id.btnGoAlarmSetting).setOnClickListener {
            startActivity(Intent(this, AlarmSettingActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnGoCharacter).setOnClickListener {
            startActivity(Intent(this, CharacterActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnResetData).setOnClickListener { confirmResetData() }
        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener { confirmLogout() }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            val user = db.userDao().getById(userId) ?: return@launch
            etNickname.setText(user.nickname)
            etWeight.setText(user.weight.toString())
            etDailyGoal.setText(user.dailyGoal.toString())
            val radioId = when (user.activityLevel) {
                1 -> R.id.radioLow
                3 -> R.id.radioHigh
                else -> R.id.radioNormal
            }
            radioGroupActivity.check(radioId)
        }
    }

    private fun loadStats() {
        lifecycleScope.launch {
            val user = db.userDao().getById(userId)
            val goal = user?.dailyGoal ?: 2000
            val dailyTotals = db.drinkRecordDao().getDailyTotals(userId)
            val totalWater = dailyTotals.sumOf { it.total }
            tvTotalWater.text = getString(R.string.mypage_total_water_format, totalWater)

            val totalsByDate = dailyTotals.associateBy({ it.date }, { it.total })
            tvStreak.text = getString(R.string.mypage_streak_format, calculateStreak(totalsByDate, goal))

            val pet = db.characterDao().getByUser(userId)
            tvPetLevelSummary.text = getString(R.string.mypage_pet_level_format, pet?.level ?: 1)
        }
    }

    /** 오늘부터 거슬러 올라가며 목표 달성이 끊기지 않은 연속일 수 */
    private fun calculateStreak(totalsByDate: Map<String, Int>, goal: Int): Int {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        val calendar = Calendar.getInstance()
        var streak = 0
        while (true) {
            val date = dateFormat.format(calendar.time)
            val total = totalsByDate[date] ?: 0
            if (total >= goal) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    private fun showAppVersion() {
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        tvAppVersion.text = getString(R.string.mypage_app_version_format, versionName)
    }

    private fun selectedActivityLevel(): Int {
        val radio = findViewById<android.widget.RadioButton>(radioGroupActivity.checkedRadioButtonId)
        return radio?.tag?.toString()?.toIntOrNull() ?: 2
    }

    private fun saveProfile() {
        val nickname = etNickname.text?.toString()?.trim().orEmpty()
        val weight = etWeight.text?.toString()?.toFloatOrNull()
        val goal = etDailyGoal.text?.toString()?.toIntOrNull()

        if (nickname.isEmpty() || weight == null || weight <= 0f || goal == null || goal <= 0) {
            Toast.makeText(this, R.string.mypage_invalid_input, Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val current = db.userDao().getById(userId) ?: return@launch

            val existing = db.userDao().findByNickname(nickname)
            if (existing != null && existing.id != userId) {
                etNickname.error = getString(R.string.error_nickname_taken)
                return@launch
            }

            db.userDao().update(
                current.copy(
                    nickname = nickname,
                    weight = weight,
                    activityLevel = selectedActivityLevel(),
                    dailyGoal = goal
                )
            )
            loadStats()
            Toast.makeText(this@MyPageActivity, R.string.mypage_profile_saved, Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmResetData() {
        AlertDialog.Builder(this)
            .setTitle(R.string.mypage_reset_data)
            .setMessage(R.string.mypage_reset_confirm_message)
            .setPositiveButton(R.string.mypage_confirm_yes) { _, _ -> resetData() }
            .setNegativeButton(R.string.mypage_confirm_no, null)
            .show()
    }

    /** 현재 계정의 데이터만 삭제한다. 같은 기기의 다른 계정에는 영향을 주지 않는다. */
    private fun resetData() {
        val currentUserId = userId
        lifecycleScope.launch {
            db.drinkRecordDao().clear(currentUserId)
            db.characterDao().clear(currentUserId)
            db.itemDao().clear(currentUserId)
            db.userDao().delete(currentUserId)
            prefs.clearUserData(currentUserId)
            prefs.activeUserId = PreferenceManager.NO_USER

            val intent = Intent(this@MyPageActivity, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle(R.string.mypage_logout)
            .setMessage(R.string.mypage_logout_confirm_message)
            .setPositiveButton(R.string.mypage_confirm_yes) { _, _ -> logout() }
            .setNegativeButton(R.string.mypage_confirm_no, null)
            .show()
    }

    private fun logout() {
        prefs.activeUserId = PreferenceManager.NO_USER
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
