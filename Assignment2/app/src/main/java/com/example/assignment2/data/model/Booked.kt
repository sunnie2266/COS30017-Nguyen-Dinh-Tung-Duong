package com.example.assignment2.data.model

data class Booked(
    val instrument: InstrumentItem,
    val duration: Int,
    val insurance: Boolean,
    val totalCost: Int
)