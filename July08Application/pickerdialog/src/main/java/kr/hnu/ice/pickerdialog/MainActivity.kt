package kr.hnu.ice.pickerdialog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.pickerdialog.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dateBtn.setOnClickListener {
            DatePickerDialog(this).apply {
                setOnDateSetListener { _, year, month, dayOfMonth ->
                    binding.textView2.text = "$year-${month + 1}-$dayOfMonth"
                }
            }.show()
        }

        binding.timeBtn.setOnClickListener {
            TimePickerDialog(this, { _, hourOfDay, minute ->
                binding.textView3.text = "$hourOfDay:$minute"
            }, 0, 0, true).show()
        }

        binding.digbtn.setOnClickListener {
            val dig = AlertDialog.Builder(this)
                .setTitle("Dialog")
                .setMessage("This is a dialog")
                .setPositiveButton("OK") { _, _ -> }
                .setNegativeButton("Cancel") { _, _ -> }
                .create()
            dig.show()
        }

        binding.arletbtn.setOnClickListener {
            val alert = AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("This is an alert")
                .setPositiveButton("OK") { _, _ -> }
                .create()
            alert.show() }
    }
}
