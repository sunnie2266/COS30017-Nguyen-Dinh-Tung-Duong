package com.example.assignment2.activity

import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment2.R
import com.example.assignment2.data.model.InstrumentItem
import com.example.assignment2.data.repository.UserManager

class ItemDetailsActivity : AppCompatActivity() {

    private lateinit var instrument: InstrumentItem
    private lateinit var ivInstrument: ImageView
    private lateinit var tvInstrumentName: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var tvPrice: TextView
    private lateinit var tvDescription: TextView
    private lateinit var llSpecs: LinearLayout
    private lateinit var btnBorrow: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)

        // Get instrument from intent
        instrument = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("INSTRUMENT", InstrumentItem::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("INSTRUMENT")!!
        }

        initializeViews()
        setupUI()
    }

    private fun initializeViews() {
        ivInstrument = findViewById(R.id.ivInstrument)
        tvInstrumentName = findViewById(R.id.tvInstrumentName)
        ratingBar = findViewById(R.id.ratingBar)
        tvPrice = findViewById(R.id.tvPrice)
        tvDescription = findViewById(R.id.tvDescription)
        llSpecs = findViewById(R.id.llSpecs)
        btnBorrow = findViewById(R.id.btnBorrow)
    }

    private fun setupUI() {
        ivInstrument.setImageResource(instrument.imageRes)
        tvInstrumentName.text = instrument.name
        ratingBar.rating = instrument.rating
        tvPrice.text = "${instrument.price} credits/month"
        tvDescription.text = instrument.description

        // Add specs
        instrument.specs.forEach { spec ->
            val textView = TextView(this).apply {
                text = "• $spec"
                setTextAppearance(android.R.style.TextAppearance_Medium)
                setPadding(0, 4.dpToPx(), 0, 4.dpToPx())
            }
            llSpecs.addView(textView)
        }

        btnBorrow.setOnClickListener {
                val intent = Intent(this, BookingActivity::class.java).apply {
                    putExtra("INSTRUMENT", instrument)
                }
                startActivity(intent)
        }
    }

    private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}