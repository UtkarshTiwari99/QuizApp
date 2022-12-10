package com.example.quizapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.quizapp.databinding.FragmentFeedBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val model: FeedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.questions.observe(viewLifecycleOwner) { questions ->
                    val result =questions
                    binding.btnStartQuiz.apply {
                        visibility = View.VISIBLE
                        setOnClickListener {
                                 MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Start Quiz")
                                    .setMessage("Are you sure you want to start the quiz?\nTotal questions: ${result.questions.size}\nTime limit: ${result.timeInMinutes} minutes")
                                    .setPositiveButton("Yes") { _, _ ->
                                        findNavController().navigate(R.id.action_feedFragment_to_questionFragment)
                                    }
                                    .setNegativeButton("No") { _, _ -> }
                                    .show()
                        }
                    }
                }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}