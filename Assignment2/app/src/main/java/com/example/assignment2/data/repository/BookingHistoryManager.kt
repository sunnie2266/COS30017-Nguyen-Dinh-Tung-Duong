package com.example.assignment2.data.repository

import com.example.assignment2.data.model.Booked

object BookingHistoryManager {
    private val bookings = mutableListOf<Booked>()

    fun addBooking(booking: Booked) {
        bookings.add(booking)
    }

    fun getBookings(): List<Booked> = bookings.toList()
}