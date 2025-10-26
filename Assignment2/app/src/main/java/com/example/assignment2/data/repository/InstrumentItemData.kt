package com.example.assignment2.data.repository

import com.example.assignment2.data.model.InstrumentItem
import com.example.assignment2.R

object InstrumentItemData {
    private val instruments = listOf(
        InstrumentItem(
            id = 1,
            name = "Acoustic Guitar",
            imageRes = R.drawable.guitar,
            price = 150,
            rating = 4.5f,
            category = "Acoustic",
            description = "A beautiful classic guitar with nylon strings, perfect for beginners and intermediate players. Great sound quality and comfortable to play.",
            specs = listOf("Nylon strings", "Spruce top", "Rosewood fingerboard")
        ),

        InstrumentItem(
            id = 2,
            name = "Yamaha Piano",
            imageRes = R.drawable.piano,
            price = 350,
            rating = 4.8f,
            category = "Keyboard",
            description = "Professional digital piano with weighted keys and authentic sound.",
            specs = listOf("88 keys", "Weighted action", "3 pedals")
        ),
        InstrumentItem(
            id = 3,
            name = "Fender Stratocaster",
            imageRes = R.drawable.electric_guitar,
            price = 200,
            rating = 4.3f,
            category = "Electric",
            description = "Classic electric guitar with versatile tone options.",
            specs = listOf("Maple neck", "3 single-coil pickups", "Tremolo system")
        )
    )

    fun getInstruments(): List<InstrumentItem> = instruments
}