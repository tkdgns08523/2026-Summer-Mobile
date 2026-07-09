package kr.hnu.ice.projectapplication

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kr.hnu.ice.projectapplication.database.AppDatabase
import kr.hnu.ice.projectapplication.database.DailyTotal
import kr.hnu.ice.projectapplication.util.PreferenceManager
import kr.hnu.ice.projectapplication.view.BarChartView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatisticsActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private val prefs by lazy { PreferenceManager(this) }
    private val userId get() = prefs.activeUserId

    private lateinit var chartDaily: BarChartView
    private lateinit var chartWeekly: BarChartView
    private lateinit var tvMonthAchievedDays: TextView
    private lateinit var tvMonthAverage: TextView
    private lateinit var tvMonthCompare: TextView

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    private val dayLabelFormat = SimpleDateFormat("d일", Locale.KOREA)
    private val monthFormat = SimpleDateFormat("yyyy-MM", Locale.KOREA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.statisticsRoot)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, bars.top, v.paddingRight, v.paddingBottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        chartDaily = findViewById(R.id.chartDaily)
        chartWeekly = findViewById(R.id.chartWeekly)
        tvMonthAchievedDays = findViewById(R.id.tvMonthAchievedDays)
        tvMonthAverage = findViewById(R.id.tvMonthAverage)
        tvMonthCompare = findViewById(R.id.tvMonthCompare)

        loadStatistics()
    }

    private fun loadStatistics() {
        lifecycleScope.launch {
            val user = db.userDao().getById(userId)
            val goal = (user?.dailyGoal ?: 2000).toFloat()
            val dailyTotals = db.drinkRecordDao().getDailyTotals(userId)
            val totalsByDate = dailyTotals.associateBy({ it.date }, { it.total })

            renderDailyChart(totalsByDate, goal)
            renderWeeklyChart(totalsByDate, goal)
            renderMonthSummary(dailyTotals, goal)
        }
    }

    /** 최근 7일 일별 섭취량 */
    private fun renderDailyChart(totalsByDate: Map<String, Int>, goal: Float) {
        val calendar = Calendar.getInstance()
        val values = mutableListOf<Float>()
        val labels = mutableListOf<String>()
        for (i in 6 downTo 0) {
            calendar.time = Calendar.getInstance().time
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = dateFormat.format(calendar.time)
            values.add((totalsByDate[date] ?: 0).toFloat())
            labels.add(dayLabelFormat.format(calendar.time))
        }
        chartDaily.setData(values, labels, goal)
    }

    /** 최근 4주 주간 평균 섭취량 */
    private fun renderWeeklyChart(totalsByDate: Map<String, Int>, goal: Float) {
        val values = mutableListOf<Float>()
        val labels = mutableListOf<String>()
        for (weekIndex in 3 downTo 0) {
            var sum = 0
            var days = 0
            val calendar = Calendar.getInstance()
            for (dayOffset in 0..6) {
                val totalOffset = weekIndex * 7 + dayOffset
                calendar.time = Calendar.getInstance().time
                calendar.add(Calendar.DAY_OF_YEAR, -totalOffset)
                val date = dateFormat.format(calendar.time)
                totalsByDate[date]?.let { sum += it }
                days++
            }
            values.add(sum.toFloat() / days)
            labels.add(if (weekIndex == 0) "이번주" else "${weekIndex}주전")
        }
        chartWeekly.setData(values.reversed(), labels.reversed(), goal)
    }

    /** 이번 달 요약 + 지난달 대비 비교 */
    private fun renderMonthSummary(dailyTotals: List<DailyTotal>, goal: Float) {
        val now = Calendar.getInstance()
        val thisMonth = monthFormat.format(now.time)
        val lastMonthCal = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
        val lastMonth = monthFormat.format(lastMonthCal.time)

        val thisMonthTotals = dailyTotals.filter { it.date.startsWith(thisMonth) }
        val lastMonthTotals = dailyTotals.filter { it.date.startsWith(lastMonth) }

        val achievedDays = thisMonthTotals.count { it.total >= goal }
        val thisAvg = if (thisMonthTotals.isNotEmpty()) {
            thisMonthTotals.sumOf { it.total } / thisMonthTotals.size
        } else 0
        val lastAvg = if (lastMonthTotals.isNotEmpty()) {
            lastMonthTotals.sumOf { it.total } / lastMonthTotals.size
        } else 0

        tvMonthAchievedDays.text = getString(R.string.stats_achieved_days_format, achievedDays)
        tvMonthAverage.text = getString(R.string.stats_average_format, thisAvg)

        tvMonthCompare.text = when {
            lastAvg == 0 -> getString(R.string.stats_no_last_month_data)
            thisAvg >= lastAvg -> {
                val diff = ((thisAvg - lastAvg) * 100 / lastAvg)
                getString(R.string.stats_compare_up_format, diff)
            }
            else -> {
                val diff = ((lastAvg - thisAvg) * 100 / lastAvg)
                getString(R.string.stats_compare_down_format, diff)
            }
        }
    }
}
