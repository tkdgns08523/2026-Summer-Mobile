package kr.hnu.ice.secondapplicationkt

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.worldcupImage)
        val imageUrl = "https://i.namu.wiki/i/D3ur7SKueyD8wUyLlYY15GfvFIjwbpePP60_O2F9KqW31MtZ4Dj_Wew5lVOBuz5-vjQL9Sc_sA6vNXzj5ufM6g.webp"
        Glide.with(this).load(imageUrl).into(imageView)
    }
}