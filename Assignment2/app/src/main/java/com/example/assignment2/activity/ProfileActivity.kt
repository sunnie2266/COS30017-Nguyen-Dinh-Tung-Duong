package com.example.assignment2.activity

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment2.R
import com.example.assignment2.data.repository.BookingHistoryManager
import com.example.assignment2.data.repository.UserManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserPhone: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvCredits: TextView
    private lateinit var llHistoryContainer: LinearLayout
    private lateinit var btnChangeUser: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initializeViews()
        setupUserInfo()
        setupRentalHistory()
        setupListeners()
    }

    private fun initializeViews() {
        tvUserName = findViewById(R.id.tvUserName)
        tvUserPhone = findViewById(R.id.tvUserPhone)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        tvCredits = findViewById(R.id.tvCredits)
        llHistoryContainer = findViewById(R.id.llHistoryContainer)
        btnChangeUser = findViewById(R.id.btnChangeUser)
    }

    private fun setupUserInfo() {
        val user = UserManager.getUser()
        if (user != null) {
            tvUserName.text = user.name
            tvUserPhone.text = user.phone
            tvUserEmail.text = user.email
            tvCredits.text = "Available Credits: ${user.credits}"
        }
    }

    private fun setupRentalHistory() {
        val history = BookingHistoryManager.getBookings()

        if (history.isEmpty()) {
            val textView = TextView(this).apply {
                text = "No rental history"
                setTextAppearance(android.R.style.TextAppearance_Medium)
                gravity = android.view.Gravity.CENTER
                setPadding(0, 32.dpToPx(), 0, 32.dpToPx())
            }
            llHistoryContainer.addView(textView)
        } else {
            history.forEach { booking ->
                val historyItem = LayoutInflater.from(this)
                    .inflate(R.layout.item_rental_history, llHistoryContainer, false)

                val tvInstrumentName = historyItem.findViewById<TextView>(R.id.tvInstrumentName)
                val tvRentalPeriod = historyItem.findViewById<TextView>(R.id.tvRentalPeriod)
                val tvCost = historyItem.findViewById<TextView>(R.id.tvCost)

                tvInstrumentName.text = booking.instrument.name
                tvRentalPeriod.text =
                    "From ${getCurrentDate()} • ${booking.duration} month(s)" +
                            if (booking.insurance) " • With Insurance" else ""
                tvCost.text = "${booking.totalCost} credits"

                llHistoryContainer.addView(historyItem)
            }
        }
    }

    private fun setupListeners() {
        btnChangeUser.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}