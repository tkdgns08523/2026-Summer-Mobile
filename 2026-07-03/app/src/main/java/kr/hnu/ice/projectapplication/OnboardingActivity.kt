package kr.hnu.ice.projectapplication

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import kr.hnu.ice.projectapplication.database.AppDatabase
import kr.hnu.ice.projectapplication.model.User
import kr.hnu.ice.projectapplication.model.WaterCharacter
import kr.hnu.ice.projectapplication.util.ItemCatalog
import kr.hnu.ice.projectapplication.util.PetSpecies
import kr.hnu.ice.projectapplication.util.PreferenceManager
import kr.hnu.ice.projectapplication.util.WaterCalculator

class OnboardingActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private val prefs by lazy { PreferenceManager(this) }

    private lateinit var etNickname: TextInputEditText
    private lateinit var etWeight: TextInputEditText
    private lateinit var etPetName: TextInputEditText
    private lateinit var radioGroupActivity: RadioGroup
    private lateinit var radioGroupSpecies: RadioGroup
    private lateinit var btnWakeTime: MaterialButton
    private lateinit var btnSleepTime: MaterialButton
    private lateinit var tvGoalPreview: android.widget.TextView
    private lateinit var btnStart: MaterialButton

    private var wakeHour = 7
    private var wakeMinute = 0
    private var sleepHour = 23
    private var sleepMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.onboardingRoot)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, bars.top, v.paddingRight, bars.bottom)
            insets
        }

        bindViews()
        setupTimePickers()
        setupGoalPreview()
        btnStart.setOnClickListener { submit() }
    }

    private fun bindViews() {
        etNickname = findViewById(R.id.etNickname)
        etWeight = findViewById(R.id.etWeight)
        etPetName = findViewById(R.id.etPetName)
        radioGroupActivity = findViewById(R.id.radioGroupActivity)
        radioGroupSpecies = findViewById(R.id.radioGroupSpecies)
        btnWakeTime = findViewById(R.id.btnWakeTime)
        btnSleepTime = findViewById(R.id.btnSleepTime)
        tvGoalPreview = findViewById(R.id.tvGoalPreview)
        btnStart = findViewById(R.id.btnStart)

        btnWakeTime.text = formatTime(wakeHour, wakeMinute)
        btnSleepTime.text = formatTime(sleepHour, sleepMinute)
    }

    private fun setupTimePickers() {
        btnWakeTime.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                wakeHour = hour
                wakeMinute = minute
                btnWakeTime.text = formatTime(hour, minute)
            }, wakeHour, wakeMinute, true).show()
        }
        btnSleepTime.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                sleepHour = hour
                sleepMinute = minute
                btnSleepTime.text = formatTime(hour, minute)
            }, sleepHour, sleepMinute, true).show()
        }
    }

    /** 체중/활동량이 바뀔 때마다 예상 목표량을 미리 보여준다. */
    private fun setupGoalPreview() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) = updateGoalPreview()
        }
        etWeight.addTextChangedListener(watcher)
        radioGroupActivity.setOnCheckedChangeListener { _, _ -> updateGoalPreview() }
        updateGoalPreview()
    }

    private fun updateGoalPreview() {
        val weight = etWeight.text?.toString()?.toFloatOrNull() ?: 60f
        val goal = WaterCalculator.recommendDailyGoal(weight, selectedActivityLevel())
        tvGoalPreview.text = getString(R.string.goal_preview_format, goal)
    }

    private fun selectedActivityLevel(): Int {
        val checkedId = radioGroupActivity.checkedRadioButtonId
        val radio = findViewById<android.widget.RadioButton>(checkedId)
        return radio?.tag?.toString()?.toIntOrNull() ?: WaterCalculator.ACTIVITY_NORMAL
    }

    private fun selectedSpecies(): Int {
        val checkedId = radioGroupSpecies.checkedRadioButtonId
        val radio = findViewById<android.widget.RadioButton>(checkedId)
        return radio?.tag?.toString()?.toIntOrNull() ?: PetSpecies.CHICK
    }

    private fun formatTime(hour: Int, minute: Int): String = "%02d:%02d".format(hour, minute)

    private fun submit() {
        val nickname = etNickname.text?.toString()?.trim().orEmpty()
        if (nickname.isEmpty()) {
            etNickname.error = getString(R.string.error_nickname_required)
            return
        }

        val weight = etWeight.text?.toString()?.toFloatOrNull()
        if (weight == null || weight <= 0f) {
            etWeight.error = getString(R.string.error_weight_invalid)
            return
        }

        val activityLevel = selectedActivityLevel()
        val goal = WaterCalculator.recommendDailyGoal(weight, activityLevel)
        val species = selectedSpecies()
        val petName = etPetName.text?.toString()?.trim()
            ?.ifEmpty { getString(PetSpecies.defaultNameRes(species)) }
            ?: getString(PetSpecies.defaultNameRes(species))

        btnStart.isEnabled = false
        lifecycleScope.launch {
            if (db.userDao().findByNickname(nickname) != null) {
                etNickname.error = getString(R.string.error_nickname_taken)
                btnStart.isEnabled = true
                return@launch
            }

            val newUserId = db.userDao().insert(
                User(
                    nickname = nickname,
                    weight = weight,
                    activityLevel = activityLevel,
                    dailyGoal = goal,
                    wakeTime = formatTime(wakeHour, wakeMinute),
                    sleepTime = formatTime(sleepHour, sleepMinute)
                )
            )
            db.characterDao().insert(
                WaterCharacter(userId = newUserId, name = petName, species = species)
            )
            db.itemDao().insertAll(ItemCatalog.defaultItems(newUserId))

            prefs.activeUserId = newUserId

            startActivity(Intent(this@OnboardingActivity, MainActivity::class.java))
            finish()
        }
    }
}
