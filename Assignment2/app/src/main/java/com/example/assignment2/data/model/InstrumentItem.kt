package com.example.assignment2.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InstrumentItem(
    val id: Int,
    val name: String,
    val imageRes: Int,
    val price: Int,
    val rating: Float,
    val category: String,
    val description: String,
    val specs: List<String>
) : Parcelable
