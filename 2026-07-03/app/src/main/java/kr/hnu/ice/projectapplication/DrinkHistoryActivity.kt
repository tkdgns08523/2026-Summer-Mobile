package kr.hnu.ice.projectapplication

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kr.hnu.ice.projectapplication.adapter.DrinkHistoryAdapter
import kr.hnu.ice.projectapplication.database.AppDatabase
import kr.hnu.ice.projectapplication.model.DrinkRecord
import kr.hnu.ice.projectapplication.util.DateUtil
import kr.hnu.ice.projectapplication.util.PreferenceManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DrinkHistoryActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private val prefs by lazy { PreferenceManager(this) }
    private val userId get() = prefs.activeUserId
    private val adapter = DrinkHistoryAdapter(
        onEditClick = { record -> showEditDialog(record) },
        onDeleteClick = { record ->
            lifecycleScope.launch {
                db.drinkRecordDao().delete(record)
                refreshCalendar()
            }
        }
    )

    private lateinit var tvTitle: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var btnPrevMonth: ImageButton
    private lateinit var btnNextMonth: ImageButton
    private lateinit var tvMonthLabel: TextView
    private lateinit var rowWeekdays: LinearLayout
    private lateinit var calendarDaysContainer: LinearLayout

    private var historyJob: Job? = null
    private var selectedDate: String = DateUtil.today()
    private val displayedMonth: Calendar = Calendar.getInstance()

    private val cellDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    private val monthLabelFormat = SimpleDateFormat("yyyy년 M월", Locale.KOREA)
    private val titleDateFormat = SimpleDateFormat("M월 d일", Locale.KOREA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.historyRoot)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, bars.top, v.paddingRight, bars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        tvTitle = findViewById(R.id.tvTitle)
        tvRemaining = findViewById(R.id.tvRemaining)
        tvEmpty = findViewById(R.id.tvEmpty)
        recycler = findViewById(R.id.recyclerHistory)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        btnPrevMonth = findViewById(R.id.btnPrevMonth)
        btnNextMonth = findViewById(R.id.btnNextMonth)
        tvMonthLabel = findViewById(R.id.tvMonthLabel)
        rowWeekdays = findViewById(R.id.rowWeekdays)
        calendarDaysContainer = findViewById(R.id.calendarDaysContainer)
        btnPrevMonth.setOnClickListener { changeMonth(-1) }
        btnNextMonth.setOnClickListener { changeMonth(1) }

        renderWeekdayHeader()
        observeHistory()
        lifecycleScope.launch { refreshCalendar() }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch { refreshCalendar() }
    }

    private fun changeMonth(delta: Int) {
        displayedMonth.add(Calendar.MONTH, delta)
        lifecycleScope.launch { refreshCalendar() }
    }

    private fun selectDate(dateStr: String) {
        if (selectedDate == dateStr) return
        selectedDate = dateStr
        observeHistory()
        lifecycleScope.launch { refreshCalendar() }
    }

    /** 달력에 표시할 이번 달 일별 달성 여부를 다시 불러온다. */
    private suspend fun refreshCalendar() {
        val user = db.userDao().getById(userId)
        val goal = user?.dailyGoal ?: 0
        val totalsByDate = db.drinkRecordDao().getDailyTotals(userId).associateBy({ it.date }, { it.total })
        renderCalendarGrid(totalsByDate, goal)
    }

    private fun observeHistory() {
        historyJob?.cancel()
        tvTitle.text = titleForDate(selectedDate)
        historyJob = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    db.userDao().observeById(userId),
                    db.drinkRecordDao().observeByDate(userId, selectedDate)
                ) { user, records -> user to records }
                    .collect { (user, records) ->
                        adapter.submitList(records)
                        tvEmpty.visibility = if (records.isEmpty()) View.VISIBLE else View.GONE

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

    private fun titleForDate(date: String): String {
        if (date == DateUtil.today()) return getString(R.string.history_title)
        val parsed = cellDateFormat.parse(date) ?: return getString(R.string.history_title)
        return getString(R.string.history_title_format, titleDateFormat.format(parsed))
    }

    private fun showEditDialog(record: DrinkRecord) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = getString(R.string.history_edit_hint)
            setText(record.amount.toString())
            setSelection(text.length)
        }
        val padding = dp(20)
        val container = FrameLayout(this).apply {
            setPadding(padding, padding / 2, padding, 0)
            addView(input)
        }
        AlertDialog.Builder(this)
            .setTitle(R.string.history_edit_title)
            .setView(container)
            .setPositiveButton(R.string.history_edit_confirm) { _, _ ->
                val newAmount = input.text.toString().toIntOrNull()
                if (newAmount == null || newAmount <= 0) {
                    Toast.makeText(this, R.string.history_edit_invalid, Toast.LENGTH_SHORT).show()
                } else {
                    lifecycleScope.launch {
                        db.drinkRecordDao().update(record.copy(amount = newAmount))
                        refreshCalendar()
                    }
                }
            }
            .setNegativeButton(R.string.mypage_confirm_no, null)
            .show()
    }

    private fun renderWeekdayHeader() {
        rowWeekdays.removeAllViews()
        resources.getStringArray(R.array.calendar_weekdays).forEach { label ->
            val tv = TextView(this)
            tv.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            tv.gravity = Gravity.CENTER
            tv.text = label
            tv.textSize = 12f
            tv.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            rowWeekdays.addView(tv)
        }
    }

    /** 달력 그리드를 다시 그린다: 날짜별 목표 달성 여부를 초록/회색으로 표시한다. */
    private fun renderCalendarGrid(totalsByDate: Map<String, Int>, goal: Int) {
        calendarDaysContainer.removeAllViews()

        val cal = displayedMonth.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        tvMonthLabel.text = monthLabelFormat.format(cal.time)

        val firstWeekday = cal.get(Calendar.DAY_OF_WEEK) - 1 // Calendar.SUNDAY(1) -> 0
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val todayStr = DateUtil.today()

        var row = newRow()
        var col = 0
        repeat(firstWeekday) {
            row.addView(buildDayCell(null, null, totalsByDate, goal, todayStr))
            col++
        }
        for (day in 1..daysInMonth) {
            if (col == 7) {
                calendarDaysContainer.addView(row)
                row = newRow()
                col = 0
            }
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dateStr = cellDateFormat.format(cal.time)
            row.addView(buildDayCell(day, dateStr, totalsByDate, goal, todayStr))
            col++
        }
        while (col in 1..6) {
            row.addView(buildDayCell(null, null, totalsByDate, goal, todayStr))
            col++
        }
        calendarDaysContainer.addView(row)
    }

    private fun newRow(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private fun buildDayCell(
        day: Int?,
        dateStr: String?,
        totalsByDate: Map<String, Int>,
        goal: Int,
        todayStr: String
    ): TextView {
        val cell = TextView(this)
        cell.layoutParams = LinearLayout.LayoutParams(0, dp(36), 1f).also {
            it.setMargins(dp(2), dp(2), dp(2), dp(2))
        }
        cell.gravity = Gravity.CENTER
        cell.textSize = 13f

        if (day == null || dateStr == null) return cell

        cell.text = day.toString()
        val total = totalsByDate[dateStr] ?: 0
        val achieved = goal > 0 && total >= goal
        val hasRecord = total > 0
        val isFuture = dateStr > todayStr
        val isSelected = dateStr == selectedDate

        val fillColor = when {
            achieved -> ContextCompat.getColor(this, R.color.water_blue)
            hasRecord -> ContextCompat.getColor(this, R.color.track_grey)
            else -> Color.TRANSPARENT
        }
        val textColor = when {
            achieved -> ContextCompat.getColor(this, R.color.white)
            isFuture -> ContextCompat.getColor(this, R.color.track_grey)
            else -> ContextCompat.getColor(this, R.color.text_primary)
        }
        cell.setTextColor(textColor)

        val background = GradientDrawable()
        background.shape = GradientDrawable.OVAL
        background.setColor(fillColor)
        if (isSelected) {
            background.setStroke(dp(2), ContextCompat.getColor(this, R.color.water_blue_dark))
        }
        cell.background = background

        if (!isFuture) {
            cell.setOnClickListener { selectDate(dateStr) }
        }
        return cell
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
