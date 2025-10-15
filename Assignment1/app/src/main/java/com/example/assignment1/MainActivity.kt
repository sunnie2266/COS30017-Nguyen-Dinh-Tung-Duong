package com.example.assignment1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var scoreDisplay: TextView
    private lateinit var currentHold: TextView
    private lateinit var btnClimb: Button
    private lateinit var btnFall: Button
    private lateinit var btnReset: Button

    private var score = 0
    private var hold = 0
    private var hasFallen = false
    private var reachedTop = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        scoreDisplay = findViewById(R.id.scoreDisplay)
        currentHold = findViewById(R.id.currentHold)
        btnClimb = findViewById(R.id.btnClimb)
        btnFall = findViewById(R.id.btnFall)
        btnReset = findViewById(R.id.btnReset)

        btnClimb.setOnClickListener {
            if (hasFallen || reachedTop) {
                return@setOnClickListener
            }

            hold++
            currentHold.text = "You are currently at hold: $hold"

            when (hold) {
                in 1..3 -> score += 1
                in 4..6 -> score += 2
                in 7..9 -> score += 3
            }

            if (hold >= 9) {
                reachedTop = true
                Log.i("Game", "Reached the top!")
                Toast.makeText(this, "You reached to the top!!", Toast.LENGTH_SHORT).show()
            }

            if (score >= 18) {
                score = 18
            }

            updateUI()
        }

        btnFall.setOnClickListener {
            if (reachedTop || hasFallen || hold == 0) {
                return@setOnClickListener
            }

            when (hold) {
                in 1..6 -> score -= 3
            }

            hold -= 3
            currentHold.text = "You are currently at hold: $hold"

            if (score <= 0) {
                score = 0
            }

            hasFallen = true
            Log.i("Game", "Player has fallen!")
            Toast.makeText(this, "Player has fallen...", Toast.LENGTH_SHORT).show()

            updateUI()
        }

        btnReset.setOnClickListener {
            score = 0
            hold = 0
            reachedTop = false
            hasFallen = false
            Log.i("Game", "Reset!")
            currentHold.text = "You are currently at hold: $hold"
            Toast.makeText(this, "Game Reset!", Toast.LENGTH_SHORT).show()
            updateUI()
        }

        updateUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("score", score)
        outState.putInt("hold", hold)
        outState.putBoolean("hasFallen", hasFallen)
        outState.putBoolean("reachedTop", reachedTop)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        score = savedInstanceState.getInt("score")
        hold = savedInstanceState.getInt("hold")
        hasFallen = savedInstanceState.getBoolean("hasFallen")
        reachedTop = savedInstanceState.getBoolean("reachedTop")
        updateUI()
    }

    private fun updateUI() {
        scoreDisplay.text = "Score: $score"

        val colorScore = when (hold) {
            in 1..3 -> android.R.color.holo_blue_bright
            in 4..6 -> android.R.color.holo_green_light
            in 7..9 -> android.R.color.holo_red_light
            else -> android.R.color.black
        }

        scoreDisplay.setTextColor(ContextCompat.getColor(this, colorScore))
        currentHold.setTextColor(ContextCompat.getColor(this, colorScore))
    }
}
