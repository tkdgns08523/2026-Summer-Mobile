package kr.hnu.ice.projectapplication

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import kr.hnu.ice.projectapplication.adapter.StretchingAdapter
import kr.hnu.ice.projectapplication.database.AppDatabase
import kr.hnu.ice.projectapplication.model.Stretching
import kr.hnu.ice.projectapplication.util.PreferenceManager
import kr.hnu.ice.projectapplication.util.StretchingCatalog

class StretchingActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private val prefs by lazy { PreferenceManager(this) }
    private lateinit var tvTodayCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stretching)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        tvTodayCount = findViewById(R.id.tvTodayCount)

        findViewById<RecyclerView>(R.id.recyclerStretching).apply {
            layoutManager = LinearLayoutManager(this@StretchingActivity)
            adapter = StretchingAdapter(StretchingCatalog.all()) { showTimerDialog(it) }
        }

        updateTodayCount()
    }

    private fun updateTodayCount() {
        tvTodayCount.text = getString(R.string.stretch_today_count_format, prefs.todayStretchCount())
    }

    private fun showTimerDialog(stretching: Stretching) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_stretching_timer)
        dialog.setCancelable(true)

        val tvEmoji = dialog.findViewById<TextView>(R.id.tvDialogEmoji)
        val tvTitle = dialog.findViewById<TextView>(R.id.tvDialogTitle)
        val tvDescription = dialog.findViewById<TextView>(R.id.tvDialogDescription)
        val tvTimer = dialog.findViewById<TextView>(R.id.tvDialogTimer)
        val btnStart = dialog.findViewById<MaterialButton>(R.id.btnDialogStart)
        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnDialogCancel)

        tvEmoji.text = stretching.emoji
        tvTitle.text = stretching.title
        tvDescription.text = stretching.description
        tvTimer.text = stretching.durationSeconds.toString()

        var countDownTimer: CountDownTimer? = null

        btnStart.setOnClickListener {
            btnStart.isEnabled = false
            countDownTimer = object : CountDownTimer(stretching.durationSeconds * 1000L, 1000L) {
                override fun onTick(millisUntilFinished: Long) {
                    tvTimer.text = (millisUntilFinished / 1000 + 1).toString()
                }

                override fun onFinish() {
                    tvTimer.text = "0"
                    completeStretch(stretching)
                    dialog.dismiss()
                }
            }.also { it.start() }
        }

        btnCancel.setOnClickListener {
            countDownTimer?.cancel()
            dialog.dismiss()
        }

        dialog.setOnDismissListener { countDownTimer?.cancel() }

        dialog.show()
    }

    private fun completeStretch(stretching: Stretching) {
        prefs.incrementTodayStretchCount()
        prefs.addCoins(REWARD_COINS)
        updateTodayCount()

        lifecycleScope.launch {
            db.characterDao().getByUser(prefs.activeUserId)?.let { pet ->
                var exp = pet.exp + REWARD_EXP
                var level = pet.level
                while (exp >= level * 100) {
                    exp -= level * 100
                    level++
                }
                db.characterDao().update(pet.copy(exp = exp, level = level))
            }
        }
    }

    companion object {
        private const val REWARD_COINS = 5
        private const val REWARD_EXP = 15
    }
}
