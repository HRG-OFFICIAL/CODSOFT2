package com.example.quotes

data class Quote(
    val text: String,
    val author: String,
    val category: QuoteCategory,
    val imageResId: Int  // e.g., R.drawable.inspiration1
)
