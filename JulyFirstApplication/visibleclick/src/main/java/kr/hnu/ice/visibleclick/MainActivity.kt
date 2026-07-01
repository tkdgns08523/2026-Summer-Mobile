package kr.hnu.ice.visibleclick

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val Visiblebtn : Button = findViewById(R.id.VIsiblebtn)
        val Invisiblebtn : Button = findViewById(R.id.Invisiblebtn)
        val Gonebtn : Button = findViewById(R.id.Gonebtn)
        val Targetbtn : Button = findViewById(R.id.Targetbtn)

        Visiblebtn.setOnClickListener {
            Targetbtn.visibility = Button.VISIBLE
        }
        Invisiblebtn.setOnClickListener {
            Targetbtn.visibility = Button.INVISIBLE
        }
        Gonebtn.setOnClickListener {
            Targetbtn.visibility = Button.GONE
        }
    }
}