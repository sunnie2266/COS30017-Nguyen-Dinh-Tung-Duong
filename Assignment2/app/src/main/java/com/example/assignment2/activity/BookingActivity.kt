package com.example.assignment2.activity

import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment2.R
import com.example.assignment2.data.model.Booked
import com.example.assignment2.data.model.InstrumentItem
import com.example.assignment2.data.repository.BookingHistoryManager
import com.example.assignment2.data.repository.UserManager
import com.google.android.material.switchmaterial.SwitchMaterial

class BookingActivity : AppCompatActivity() {

    private lateinit var instrument: InstrumentItem
    private lateinit var tvInstrumentName: TextView
    private lateinit var tvPrice: TextView
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var radioGroupDuration: RadioGroup
    private lateinit var radio1Month: RadioButton
    private lateinit var radio3Months: RadioButton
    private lateinit var radio6Months: RadioButton
    private lateinit var switchInsurance: SwitchMaterial
    private lateinit var tvTotalCost: TextView
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button

    private var duration = 1
    private var insurance = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booking_activity)

        instrument = intent.getParcelableExtra("INSTRUMENT")!!

        initializeViews()
        setupUI()
        setupListeners()
        calculateTotalCost()
    }

    private fun initializeViews() {
        tvInstrumentName = findViewById(R.id.tvInstrumentName)
        tvPrice = findViewById(R.id.tvPrice)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        radioGroupDuration = findViewById(R.id.radioGroupDuration)
        radio1Month = findViewById(R.id.radio1Month)
        radio3Months = findViewById(R.id.radio3Months)
        radio6Months = findViewById(R.id.radio6Months)
        switchInsurance = findViewById(R.id.switchInsurance)
        tvTotalCost = findViewById(R.id.tvTotalCost)
        btnCancel = findViewById(R.id.btnCancel)
        btnSave = findViewById(R.id.btnSave)
    }

    private fun setupUI() {
        tvInstrumentName.text = instrument.name
        tvPrice.text = "${instrument.price} credits/month"

        // Pre-fill user info
        val user = UserManager.getUser()
        etName.setText(user?.name ?: "")
        etPhone.setText(user?.phone ?: "")
        etEmail.setText(user?.email ?: "")

        // Set default duration
        radio1Month.isChecked = true
    }

    private fun setupListeners() {
        radioGroupDuration.setOnCheckedChangeListener { _, checkedId ->
            duration = when (checkedId) {
                R.id.radio1Month -> 1
                R.id.radio3Months -> 3
                R.id.radio6Months -> 6
                else -> 1
            }
            calculateTotalCost()
        }

        switchInsurance.setOnCheckedChangeListener { _, isChecked ->
            insurance = isChecked
            calculateTotalCost()
        }

        btnCancel.setOnClickListener {
            showCancellationToast()
            finish()
        }

        btnSave.setOnClickListener {
            if (validateInput() && validateCredits()) {
                saveBooking()
            }
        }
    }

    private fun calculateTotalCost() {
        var total = instrument.price * duration
        if (insurance) {
            total += 50 * duration
        }
        tvTotalCost.text = "Total Cost: $total credits"
    }

    private fun validateInput(): Boolean {
        val name = etName.text.toString()
        val phone = etPhone.text.toString()
        val email = etEmail.text.toString()

        if (name.isBlank()) {
            etName.error = "Name is required"
            return false
        }

        if (phone.isBlank()) {
            etPhone.error = "Phone is required"
            return false
        }

        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Valid email is required"
            return false
        }

        return true
    }

    private fun validateCredits(): Boolean {
        val user = UserManager.getUser()
        val totalCost = calculateTotalCostValue()

        if (user == null) {
            showError("User not found")
            return false
        }

        if (user.credits < totalCost) {
            showError("Insufficient credits. You have ${user.credits} credits but need $totalCost")
            return false
        }

        return true
    }

    private fun calculateTotalCostValue(): Int {
        var total = instrument.price * duration
        if (insurance) {
            total += 50 * duration
        }
        return total
    }

    private fun saveBooking() {
        val totalCost = calculateTotalCostValue()
        val booking = Booked(instrument, duration, insurance, totalCost)

        val user = UserManager.getUser()!!
        UserManager.updateCredits(user.credits - totalCost)

        BookingHistoryManager.addBooking(booking)

        setResult(RESULT_OK)
        finish()
    }

    private fun showCancellationToast() {
        Toast.makeText(
            this,
            "Booking cancelled",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}