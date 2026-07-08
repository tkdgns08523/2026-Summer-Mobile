package kr.hnu.ice.projectapplication

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kr.hnu.ice.projectapplication.adapter.RankingAdapter
import kr.hnu.ice.projectapplication.database.AppDatabase
import kr.hnu.ice.projectapplication.model.Friend
import kr.hnu.ice.projectapplication.util.DateUtil
import kr.hnu.ice.projectapplication.util.PetSpecies
import kr.hnu.ice.projectapplication.util.PreferenceManager
import kr.hnu.ice.projectapplication.util.RankingCatalog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 서버가 없으므로, 이 기기에 로컬로 저장된 모든 계정 + 목업 친구 데이터(RankingCatalog)를 함께 랭킹으로 보여준다.
 * 현재 로그인한 계정도 다른 계정들과 함께 표시된다.
 */
class RankingActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private val prefs by lazy { PreferenceManager(this) }

    private lateinit var recycler: RecyclerView
    private lateinit var tvRewardBanner: TextView

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        recycler = findViewById<RecyclerView>(R.id.recyclerRanking).apply {
            layoutManager = LinearLayoutManager(this@RankingActivity)
        }
        tvRewardBanner = findViewById(R.id.tvRewardBanner)

        loadRanking()
    }

    private fun loadRanking() {
        lifecycleScope.launch {
            val accounts = db.userDao().getAll()
            val realFriends = accounts.map { user ->
                val rate = calculateWeeklyRate(user.id, user.dailyGoal)
                val pet = db.characterDao().getByUser(user.id)
                Friend(
                    nickname = user.nickname,
                    petEmoji = PetSpecies.emojiForLevel(pet?.species ?: PetSpecies.CHICK, pet?.level ?: 1),
                    achievementRate = rate,
                    isMe = user.id == prefs.activeUserId
                )
            }

            // 서버가 없으므로 목업 친구 데이터를 함께 섞어 랭킹 화면을 채운다.
            val allFriends = realFriends + RankingCatalog.mockFriends()
            val sorted = allFriends.sortedByDescending { it.achievementRate }
            recycler.adapter = RankingAdapter(sorted)

            showRewardBannerIfTop(sorted)
        }
    }

    /** 최근 7일 목표 달성률 평균(%) */
    private suspend fun calculateWeeklyRate(userId: Long, goal: Int): Int {
        val totalsByDate = db.drinkRecordDao().getDailyTotals(userId).associateBy({ it.date }, { it.total })
        val calendar = Calendar.getInstance()
        var sumPercent = 0
        for (i in 0..6) {
            calendar.time = Calendar.getInstance().time
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = dateFormat.format(calendar.time)
            val total = totalsByDate[date] ?: 0
            sumPercent += (total * 100 / goal).coerceIn(0, 100)
        }
        return sumPercent / 7
    }

    private fun showRewardBannerIfTop(sorted: List<Friend>) {
        val myRank = sorted.indexOfFirst { it.isMe } + 1
        val alreadyClaimed = prefs.rankRewardClaimedWeek == DateUtil.currentWeekKey()

        if (myRank == 1 && !alreadyClaimed) {
            tvRewardBanner.visibility = android.view.View.VISIBLE
            tvRewardBanner.text = getString(R.string.ranking_reward_banner)
            tvRewardBanner.setOnClickListener {
                prefs.addCoins(RANK_REWARD_COINS)
                prefs.rankRewardClaimedWeek = DateUtil.currentWeekKey()
                tvRewardBanner.visibility = android.view.View.GONE
                Toast.makeText(this, getString(R.string.ranking_reward_claimed, RANK_REWARD_COINS), Toast.LENGTH_SHORT).show()
            }
        } else {
            tvRewardBanner.visibility = android.view.View.GONE
        }
    }

    companion object {
        private const val RANK_REWARD_COINS = 100
    }
}
