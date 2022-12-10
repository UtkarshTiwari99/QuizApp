package com.example.quizapp.models

import androidx.annotation.Keep

@Keep
data class Result(
    val questions: List<Question>,
    val timeInMinutes: Int
)