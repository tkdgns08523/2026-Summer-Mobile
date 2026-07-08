package kr.hnu.ice.projectapplication

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kr.hnu.ice.projectapplication.adapter.ItemShopAdapter
import kr.hnu.ice.projectapplication.database.AppDatabase
import kr.hnu.ice.projectapplication.model.Item
import kr.hnu.ice.projectapplication.model.WaterCharacter
import kr.hnu.ice.projectapplication.util.ItemCatalog
import kr.hnu.ice.projectapplication.util.PetSpecies
import kr.hnu.ice.projectapplication.util.PreferenceManager
import kr.hnu.ice.projectapplication.util.parseColorOrNull

class CharacterActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private val prefs by lazy { PreferenceManager(this) }
    private val userId get() = prefs.activeUserId
    private val shopAdapter = ItemShopAdapter(onItemClick = { onItemClicked(it) })

    private lateinit var tvPetEmoji: TextView
    private lateinit var vPetGlow: View
    private lateinit var vPetBackgroundColor: View
    private lateinit var tvPetName: TextView
    private lateinit var tvPetLevel: TextView
    private lateinit var tvCoins: TextView
    private lateinit var tvCollection: TextView
    private lateinit var progressExp: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        tvPetEmoji = findViewById(R.id.tvPetEmoji)
        vPetGlow = findViewById(R.id.vPetGlow)
        vPetBackgroundColor = findViewById(R.id.vPetBackgroundColor)
        tvPetName = findViewById(R.id.tvPetName)
        tvPetLevel = findViewById(R.id.tvPetLevel)
        tvCoins = findViewById(R.id.tvCoins)
        tvCollection = findViewById(R.id.tvCollection)
        progressExp = findViewById(R.id.progressExp)

        findViewById<RecyclerView>(R.id.recyclerShop).apply {
            layoutManager = GridLayoutManager(this@CharacterActivity, 2)
            adapter = shopAdapter
        }

        lifecycleScope.launch { seedItemsIfNeeded() }
        observePetAndItems()
    }

    override fun onResume() {
        super.onResume()
        updateCoinsLabel()
    }

    private suspend fun seedItemsIfNeeded() {
        // 예전 버전에서 만들어진 모자/액세서리 아이템은 색상 hex로 해석할 수 없으니 정리한다.
        db.itemDao().deleteLegacyNonBackgroundItems()
        if (db.itemDao().count(userId) == 0) {
            db.itemDao().insertAll(ItemCatalog.defaultItems(userId))
        }
    }

    private fun observePetAndItems() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    db.characterDao().observeByUser(userId),
                    db.itemDao().observeAll(userId)
                ) { pet, items -> pet to items }
                    .collect { (pet, items) ->
                        if (pet != null) bindPet(pet, items)
                        shopAdapter.submitList(items)
                    }
            }
        }
    }

    private fun bindPet(pet: WaterCharacter, items: List<Item>) {
        tvPetEmoji.text = PetSpecies.emojiForLevel(pet.species, pet.level)
        tvPetName.text = pet.name
        tvPetLevel.text = getString(R.string.character_level_mood_format, pet.level, moodLabel(pet.mood))
        progressExp.progress = (pet.levelProgress * 100).toInt()
        val stages = PetSpecies.collectionStages(pet.species)
        val thresholds = PetSpecies.collectionThresholds()
        tvCollection.text = stages.mapIndexed { index, emoji ->
            if (pet.level >= thresholds[index]) emoji else LOCKED_EMOJI
        }.joinToString(" ")

        val backgroundColor = items.firstOrNull { it.isEquipped }?.emoji?.let(::parseColorOrNull)
        if (backgroundColor != null) {
            vPetBackgroundColor.background.setTint(backgroundColor)
            vPetBackgroundColor.visibility = View.VISIBLE
            vPetGlow.visibility = View.GONE
        } else {
            vPetBackgroundColor.visibility = View.GONE
            vPetGlow.visibility = View.VISIBLE
        }
    }

    private fun updateCoinsLabel() {
        tvCoins.text = getString(R.string.coins_format, prefs.coins)
    }

    private fun onItemClicked(item: Item) {
        lifecycleScope.launch {
            when {
                item.isEquipped -> {
                    db.itemDao().update(item.copy(isEquipped = false))
                }
                item.isOwned -> {
                    db.itemDao().unequipType(userId, item.type)
                    db.itemDao().update(item.copy(isEquipped = true))
                }
                prefs.coins >= item.price -> {
                    prefs.addCoins(-item.price)
                    db.itemDao().unequipType(userId, item.type)
                    db.itemDao().update(item.copy(isOwned = true, isEquipped = true))
                    updateCoinsLabel()
                    Toast.makeText(this@CharacterActivity, R.string.item_purchased, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this@CharacterActivity, R.string.item_not_enough_coins, Toast.LENGTH_SHORT).show()
                    return@launch
                }
            }
            // Flow 갱신을 기다리지 않고, 착용 상태를 즉시 화면에 반영한다.
            db.characterDao().getByUser(userId)?.let { pet -> bindPet(pet, db.itemDao().getAll(userId)) }
        }
    }

    private fun moodLabel(mood: Int): String = when {
        mood >= 70 -> getString(R.string.mood_happy)
        mood >= 40 -> getString(R.string.mood_normal)
        else -> getString(R.string.mood_sad)
    }

    companion object {
        private const val LOCKED_EMOJI = "❔"
    }
}
