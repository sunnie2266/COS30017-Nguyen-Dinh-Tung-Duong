package com.example.assignment2.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment2.R
import android.view.View
import com.example.assignment2.data.repository.InstrumentItemData
import com.example.assignment2.data.repository.UserManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.example.assignment2.data.repository.BookingHistoryManager
import com.google.android.material.snackbar.Snackbar

class InstrumentItemActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var btnProfile: Button
    private lateinit var btnChangeUser: Button
    private lateinit var ivInstrument: ImageView
    private lateinit var tvInstrumentName: TextView
    private lateinit var tvPrice: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var chipGroupCategory: ChipGroup
    private lateinit var chipAcoustic: Chip
    private lateinit var chipElectric: Chip
    private lateinit var chipKeyboard: Chip
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var btnViewDetails: Button
    private lateinit var btnBorrow: Button

    private val instruments = InstrumentItemData.getInstruments()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instrument_item)

        initializeViews()
        setupUserInfo()
        setupNavigation()
        displayCurrentInstrument()
    }

    private fun initializeViews() {
        tvUserName = findViewById(R.id.tvUserName)
        btnProfile = findViewById(R.id.btnProfile)
        btnChangeUser = findViewById(R.id.btnChangeUser)
        ivInstrument = findViewById(R.id.ivInstrument)
        tvInstrumentName = findViewById(R.id.tvInstrumentName)
        tvPrice = findViewById(R.id.tvPrice)
        ratingBar = findViewById(R.id.ratingBar)
        chipGroupCategory = findViewById(R.id.chipGroupCategory)
        chipAcoustic = findViewById(R.id.chipAcoustic)
        chipElectric = findViewById(R.id.chipElectric)
        chipKeyboard = findViewById(R.id.chipKeyboard)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        btnViewDetails = findViewById(R.id.btnViewDetails)
        btnBorrow = findViewById(R.id.btnBorrow)
    }

    private fun setupUserInfo() {
        val user = UserManager.getUser()
        tvUserName.text = user?.name ?: "Guest"

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnChangeUser.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        showRecentBookingNotification()
    }

    private fun showRecentBookingNotification() {
        val recentBooking = BookingHistoryManager.getBookings().lastOrNull()
        if (recentBooking != null) {
            val rootView = findViewById<View>(android.R.id.content)
            Snackbar.make(rootView,
                "Booked successfully! Recently booked: ${recentBooking.instrument.name}",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
    private fun setupNavigation() {
        btnPrevious.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                displayCurrentInstrument()
            }
        }

        btnNext.setOnClickListener {
            if (currentIndex < instruments.size - 1) {
                currentIndex++
                displayCurrentInstrument()
            }
        }

        btnViewDetails.setOnClickListener {
            val intent = Intent(this, ItemDetailsActivity::class.java).apply {
                putExtra("INSTRUMENT", instruments[currentIndex])
            }
            startActivity(intent)
        }

        btnBorrow.setOnClickListener {
                val intent = Intent(this, BookingActivity::class.java).apply {
                    putExtra("INSTRUMENT", instruments[currentIndex])
                }
                startActivity(intent)
        }

        chipAcoustic.setOnClickListener {
            jumpToFirstInstrumentOfCategory("Acoustic")
        }

        chipElectric.setOnClickListener {
            jumpToFirstInstrumentOfCategory("Electric")
        }

        chipKeyboard.setOnClickListener {
            jumpToFirstInstrumentOfCategory("Keyboard")
        }
    }

    private fun jumpToFirstInstrumentOfCategory(category: String) {
        val index = instruments.indexOfFirst { it.category == category }
        if (index != -1) {
            currentIndex = index
            displayCurrentInstrument()
            Toast.makeText(this, "Jumped to $category", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No $category instruments", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayCurrentInstrument() {
        val instrument = instruments[currentIndex]

        ivInstrument.setImageResource(instrument.imageRes)
        tvInstrumentName.text = instrument.name
        tvPrice.text = "${instrument.price} credits/month"
        ratingBar.rating = instrument.rating

        chipGroupCategory.clearCheck()
        when (instrument.category) {
            "Acoustic" -> chipAcoustic.isChecked = true
            "Electric" -> chipElectric.isChecked = true
            "Keyboard" -> chipKeyboard.isChecked = true
        }

        btnPrevious.isEnabled = currentIndex > 0
        btnNext.isEnabled = currentIndex < instruments.size - 1
    }
}