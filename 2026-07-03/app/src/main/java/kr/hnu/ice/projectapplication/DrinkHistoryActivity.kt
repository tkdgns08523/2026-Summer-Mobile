package kr.hnu.ice.projectapplication

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kr.hnu.ice.projectapplication.adapter.DrinkHistoryAdapter
import kr.hnu.ice.projectapplication.database.AppDatabase
import kr.hnu.ice.projectapplication.util.DateUtil
import kr.hnu.ice.projectapplication.util.PreferenceManager

class DrinkHistoryActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private val prefs by lazy { PreferenceManager(this) }
    private val userId get() = prefs.activeUserId
    private val adapter = DrinkHistoryAdapter(onDeleteClick = { record ->
        lifecycleScope.launch { db.drinkRecordDao().delete(record) }
    })

    private lateinit var tvRemaining: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.historyRoot)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, bars.top, v.paddingRight, bars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        tvRemaining = findViewById(R.id.tvRemaining)
        tvEmpty = findViewById(R.id.tvEmpty)
        recycler = findViewById(R.id.recyclerHistory)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        observeHistory()
    }

    private fun observeHistory() {
        val today = DateUtil.today()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    db.userDao().observeById(userId),
                    db.drinkRecordDao().observeByDate(userId, today)
                ) { user, records -> user to records }
                    .collect { (user, records) ->
                        adapter.submitList(records)
                        tvEmpty.visibility = if (records.isEmpty()) {
                            android.view.View.VISIBLE
                        } else {
                            android.view.View.GONE
                        }

                        val goal = user?.dailyGoal ?: 0
                        val total = records.sumOf { it.amount }
                        val remaining = goal - total
                        tvRemaining.text = if (remaining <= 0) {
                            getString(R.string.history_remaining_done)
                        } else {
                            getString(R.string.history_remaining_format, remaining)
                        }
                    }
            }
        }
    }
}
