package com.example.quizapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.models.Option
import com.example.quizapp.models.Question
import kotlinx.coroutines.launch

class FeedViewModel: ViewModel() {

    private val _questions = MutableLiveData<com.example.quizapp.models.Result>()
    val questions get() = _questions

    init {
        getQuestions()
    }

    private fun getQuestions() {
        val q1= Question(2, "What is the speed of sound?", listOf(Option(1,"120 km/h"), Option(2," 1200 km/h"), Option(3,"400 km/h"), Option(4,"700 km/h")),R.drawable.q1)
        val q2=Question(3,"What is actually electricity?", listOf(Option(1,"A flow of water"), Option(2,"A flow of air"), Option(3,"A flow of electrons")),R.drawable.q2)
        val q3=Question(3,"What do we call a newly hatched butterfly?", listOf(Option(1," A moth"),Option(2,"A butter"),Option(3,"A caterpillar"),Option(4,"A chrysalis")),R.drawable.q3)
        val q4=Question(2," Which did Viking people use as money?", listOf(Option(1,"Rune stones"),Option(2,"Jewellery"),Option(3,"Seal skins")),R.drawable.q4)
        val r=com.example.quizapp.models.Result(listOf(q1,q2,q3,q4),1)
        _questions.postValue(r)
    }
}