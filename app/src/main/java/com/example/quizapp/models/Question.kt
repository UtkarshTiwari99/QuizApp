package com.example.quizapp.models

import androidx.annotation.Keep

@Keep
data class Question(
    val correct_answer: Int,
    val lable: String,
    val options: List<Option>
)