package com.example.quizapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.quizapp.databinding.FragmentQuestionBinding
import com.example.quizapp.databinding.ItemOptionBinding
import com.example.quizapp.models.Question
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!

    private val feedModel by activityViewModels<FeedViewModel>()
    private val questionModel by activityViewModels<QuestionViewModel>()
    private var selectedOptions:Int?=null
    private lateinit var timer: CountDownTimer
    private var score = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        questionModel.questionNumber.postValue(0)

        feedModel.questions.observe(viewLifecycleOwner) {
            if (it!= null) {
                val questions = it.questions
                val totalQuestions = questions.size
                binding.lpiProgress.max = totalQuestions

                val quizTime = it.timeInMinutes
                timer = object : CountDownTimer((quizTime*60*1000).toLong(), 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val minutes = millisUntilFinished / 1000 / 60
                        val seconds = millisUntilFinished / 1000 % 60
                        binding.tvTimeRemaining.text = getString(R.string.time_left, minutes, seconds)
                    }
                    override fun onFinish() {
                        binding.tvTimeRemaining.text = getString(R.string.time_up)
                        showResult(totalQuestions)
                    }
                }.start()

                questionModel.questionNumber.observe(viewLifecycleOwner) { qNum ->
                    val question = questions[qNum]
                    binding.lpiProgress.progress = qNum + 1
                    updateQuestion(question)

                    binding.tvQuestionNumber.text = getString(
                        R.string.question_progress,
                        if ((qNum + 1).toString().length > 1) (qNum + 1).toString() else "0${(qNum + 1)}",
                        if (totalQuestions.toString().length > 1) totalQuestions.toString() else "0$totalQuestions"
                    )

                    binding.btnNext.text = if (qNum == totalQuestions-1) "Submit" else getString(R.string.next)
                    binding.btnNext.setOnClickListener {
                        if (qNum == totalQuestions-1) {
                            timer.cancel()
                            evaluateQuizResult(questions[qNum].correct_answer)
                            showResult(totalQuestions)
                        } else {
                            if (selectedOptions!=null) {
                                evaluateQuizResult(questions[qNum].correct_answer)
                                resetOptions()
                            }
                            questionModel.questionNumber.postValue(qNum + 1)
                        }
                    }
                }

            } else {
                binding.ibBack.performClick()
            }
        }

        binding.ibBack.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("You will lose your progress")
                .setPositiveButton("Yes") { _, _ ->
                    findNavController().navigate(R.id.action_questionFragment_to_feedFragment)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.btnQuit.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("You will lose your progress")
                .setPositiveButton("Yes") { _, _ ->
                    findNavController().navigate(R.id.action_questionFragment_to_feedFragment)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun showResult(totalQuestions: Int) {
        binding.quizResult.apply {
            clResults.visibility = View.VISIBLE
            if (score == totalQuestions) {
                tvResult.text = "Congratulations!"
            } else {
                tvResult.text = "Better luck next time!"
            }
            tvScore.text = "$score/$totalQuestions"
            requireActivity().getSharedPreferences("score", Context.MODE_PRIVATE).edit().putInt("score", score).apply()

            btnShareResults.setOnClickListener {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_result, score, totalQuestions))
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }

            btnReturnToFeed.setOnClickListener {
                findNavController().navigate(R.id.action_questionFragment_to_feedFragment)
            }
        }
    }

    private fun <T:View> animates(view:T){
        val anim= TranslateAnimation(0F+ view.width,0F, 0F, 0F)
        anim.duration = 800
        view.startAnimation(anim)
    }

    private fun updateQuestion(question: Question) {
        animates(binding.questionContent)
        binding.tvQuestion.text = question.lable
        var i=question.options.size
        binding.option1.tvOption.text = question.options[0].lable
        binding.option1.llOption.setOnClickListener {
            selectOption(binding.option1, 1)
        }
        i--;
        binding.option2.tvOption.text = question.options[1].lable
        binding.option2.llOption.setOnClickListener {
            selectOption(binding.option2, 2)
        }
        i--;
        binding.option3.tvOption.text = question.options[2].lable
        binding.option3.llOption.setOnClickListener {
            selectOption(binding.option3, 3)
        }
        i--;
        if(i!=0) {
            binding.option4.root.visibility=View.VISIBLE
            binding.option4.tvOption.text = question.options[3].lable
            binding.option4.llOption.setOnClickListener {
                selectOption(binding.option4, 4)
            }
        }else
            binding.option4.root.visibility=View.GONE
    }

    private fun selectOption(view: ItemOptionBinding, option: Int) {
        if (selectedOptions!=null) {
            resetOptions()
            selectedOptions=option
            view.llOption.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.success)
        } else {
            selectedOptions=option
            view.llOption.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.success)
        }
    }

    private fun evaluateQuizResult(answer:Int) {
        if (selectedOptions == answer ) {
            score++
        }
    }

    private fun resetOptions() {
        selectedOptions=null
        binding.option1.llOption.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.shimmerDark)
        binding.option2.llOption.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.shimmerDark)
        binding.option3.llOption.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.shimmerDark)
        binding.option4.llOption.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.shimmerDark)
    }

    override fun onDestroyView() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        super.onDestroyView()
        _binding = null
    }
}