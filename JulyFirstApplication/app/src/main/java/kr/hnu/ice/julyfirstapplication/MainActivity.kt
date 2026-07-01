package kr.hnu.ice.julyfirstapplication

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Create the three content views with no extra padding
    val name = TextView(this).apply {
        text = "Hello, World!"
        textSize = 24f
        gravity = Gravity.CENTER
        includeFontPadding = false
        setPadding(0, 0, 0, 0)
        minHeight = 0
    }

    val image = ImageView(this).apply {
        setImageResource(R.drawable.morain_lake)
        scaleType = ImageView.ScaleType.CENTER_CROP
        adjustViewBounds = true
    }

    val address = TextView(this).apply {
        text = "Address: 123 Main St, City, Country"
        textSize = 16f
        gravity = Gravity.CENTER
        includeFontPadding = false
        setPadding(0, 0, 0, 0)
        minHeight = 0
    }

    // Layout params (no margins) to ensure views are tightly stacked
    val childLp = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
        gravity = Gravity.CENTER
    }

    val density = resources.displayMetrics.density
    // Make image use wrap_content so it doesn't introduce extra fixed space
    val imageLp = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
        gravity = Gravity.CENTER
    }

    // Each view wrapped in its own container without padding
    val nameContainer = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        setPadding(0, 0, 0, 0)
        addView(name, childLp)
    }

    val imageContainer = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        setPadding(0, 0, 0, 0)
        addView(image, imageLp)
    }

    val addressContainer = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        setPadding(0, 0, 0, 0)
        addView(address, childLp)
    }

    // Group container centered in the screen, no extra spacing and no baseline alignment
    val groupLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        isBaselineAligned = false
        setPadding(0, 0, 0, 0)
        // explicitly use WRAP_CONTENT params for tight stacking
        addView(nameContainer, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
        addView(imageContainer, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
        addView(addressContainer, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
    }

    // Root vertical LinearLayout split into two equal sections
    val root = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        weightSum = 2f
        setPadding(0, 0, 0, 0)
        // Top section: group centered
        addView(LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f)
            setPadding(0, 0, 0, 0)
            addView(groupLayout)
        })
        // Bottom section: horizontal LinearLayout with buttons
        addView(LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f)
            setPadding(0, 0, 0, 0)
            weightSum = 3f // for 3 buttons, adjust as needed
            for (i in 1..3) {
                addView(Button(this@MainActivity).apply {
                    text = "Button $i"
                    layoutParams = LinearLayout.LayoutParams(0, MATCH_PARENT, 1f)
                    setPadding(0, 0, 0, 0)
                })
            }
        })
    }

    setContentView(root)
    }
}