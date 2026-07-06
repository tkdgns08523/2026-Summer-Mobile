package kr.hnu.ice.projectapplication

import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import kr.hnu.ice.projectapplication.util.AlarmScheduler
import kr.hnu.ice.projectapplication.util.NotificationHelper
import kr.hnu.ice.projectapplication.util.PreferenceManager

class AlarmSettingActivity : AppCompatActivity() {

    private val prefs by lazy { PreferenceManager(this) }

    private lateinit var switchNotifications: MaterialSwitch
    private lateinit var radioGroupInterval: RadioGroup
    private lateinit var btnActiveStart: MaterialButton
    private lateinit var btnActiveEnd: MaterialButton
    private lateinit var switchStretch: MaterialSwitch
    private lateinit var radioGroupSound: RadioGroup
    private lateinit var switchDnd: MaterialSwitch
    private lateinit var btnDndStart: MaterialButton
    private lateinit var btnDndEnd: MaterialButton

    private var activeStart = 8 * 60
    private var activeEnd = 22 * 60
    private var dndStart = 22 * 60
    private var dndEnd = 7 * 60

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_setting)
        NotificationHelper.createChannel(this)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        bindViews()
        loadPrefs()
        setupTimeButtons()

        findViewById<MaterialButton>(R.id.btnSaveAlarm).setOnClickListener { save() }
    }

    private fun bindViews() {
        switchNotifications = findViewById(R.id.switchNotifications)
        radioGroupInterval = findViewById(R.id.radioGroupInterval)
        btnActiveStart = findViewById(R.id.btnActiveStart)
        btnActiveEnd = findViewById(R.id.btnActiveEnd)
        switchStretch = findViewById(R.id.switchStretch)
        radioGroupSound = findViewById(R.id.radioGroupSound)
        switchDnd = findViewById(R.id.switchDnd)
        btnDndStart = findViewById(R.id.btnDndStart)
        btnDndEnd = findViewById(R.id.btnDndEnd)
    }

    private fun loadPrefs() {
        switchNotifications.isChecked = prefs.notificationsEnabled
        switchStretch.isChecked = prefs.stretchNotificationsEnabled
        switchDnd.isChecked = prefs.dndEnabled

        val intervalRadioId = when (prefs.alarmIntervalMinutes) {
            30 -> R.id.radioInterval30
            120 -> R.id.radioInterval120
            else -> R.id.radioInterval60
        }
        radioGroupInterval.check(intervalRadioId)

        val soundRadioId = when (prefs.soundIndex) {
            1 -> R.id.radioSoundChime
            2 -> R.id.radioSoundSilent
            else -> R.id.radioSoundDefault
        }
        radioGroupSound.check(soundRadioId)

        activeStart = prefs.activeStartMinutes
        activeEnd = prefs.activeEndMinutes
        dndStart = prefs.dndStartMinutes
        dndEnd = prefs.dndEndMinutes

        btnActiveStart.text = formatMinutes(activeStart)
        btnActiveEnd.text = formatMinutes(activeEnd)
        btnDndStart.text = formatMinutes(dndStart)
        btnDndEnd.text = formatMinutes(dndEnd)
    }

    private fun setupTimeButtons() {
        btnActiveStart.setOnClickListener {
            pickTime(activeStart) { minutes -> activeStart = minutes; btnActiveStart.text = formatMinutes(minutes) }
        }
        btnActiveEnd.setOnClickListener {
            pickTime(activeEnd) { minutes -> activeEnd = minutes; btnActiveEnd.text = formatMinutes(minutes) }
        }
        btnDndStart.setOnClickListener {
            pickTime(dndStart) { minutes -> dndStart = minutes; btnDndStart.text = formatMinutes(minutes) }
        }
        btnDndEnd.setOnClickListener {
            pickTime(dndEnd) { minutes -> dndEnd = minutes; btnDndEnd.text = formatMinutes(minutes) }
        }
    }

    private fun pickTime(currentMinutes: Int, onPicked: (Int) -> Unit) {
        val hour = currentMinutes / 60
        val minute = currentMinutes % 60
        TimePickerDialog(this, { _, h, m -> onPicked(h * 60 + m) }, hour, minute, true).show()
    }

    private fun formatMinutes(minutes: Int): String = "%02d:%02d".format(minutes / 60, minutes % 60)

    private fun selectedInterval(): Int {
        val radio = findViewById<android.widget.RadioButton>(radioGroupInterval.checkedRadioButtonId)
        return radio?.tag?.toString()?.toIntOrNull() ?: 60
    }

    private fun selectedSoundIndex(): Int = when (radioGroupSound.checkedRadioButtonId) {
        R.id.radioSoundChime -> 1
        R.id.radioSoundSilent -> 2
        else -> 0
    }

    private fun save() {
        prefs.notificationsEnabled = switchNotifications.isChecked
        prefs.stretchNotificationsEnabled = switchStretch.isChecked
        prefs.dndEnabled = switchDnd.isChecked
        prefs.alarmIntervalMinutes = selectedInterval()
        prefs.soundIndex = selectedSoundIndex()
        prefs.activeStartMinutes = activeStart
        prefs.activeEndMinutes = activeEnd
        prefs.dndStartMinutes = dndStart
        prefs.dndEndMinutes = dndEnd

        if (switchNotifications.isChecked) {
            requestNotificationPermissionIfNeeded()
            AlarmScheduler.reschedule(this, prefs.alarmIntervalMinutes, prefs.stretchNotificationsEnabled)
        } else {
            AlarmScheduler.cancelAll(this)
        }

        Toast.makeText(this, R.string.alarm_saved, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
