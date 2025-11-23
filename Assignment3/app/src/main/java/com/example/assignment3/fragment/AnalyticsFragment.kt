package com.example.assignment3.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.assignment3.MainActivity
import com.example.assignment3.R
import com.example.assignment3.viewmodel.TaskViewModel

class AnalyticsFragment : Fragment() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var tvTotalTasks: TextView
    private lateinit var tvCompletedTasks: TextView
    private lateinit var tvPendingTasks: TextView
    private lateinit var progressCompletion: ProgressBar
    private lateinit var tvHighPriority: TextView
    private lateinit var tvMediumPriority: TextView
    private lateinit var tvLowPriority: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[TaskViewModel::class.java]

        tvTotalTasks = view.findViewById(R.id.tvTotalTasks)
        tvCompletedTasks = view.findViewById(R.id.tvCompletedTasks)
        tvPendingTasks = view.findViewById(R.id.tvPendingTasks)
        progressCompletion = view.findViewById(R.id.progressCompletion)
        tvHighPriority = view.findViewById(R.id.tvHighPriority)
        tvMediumPriority = view.findViewById(R.id.tvMediumPriority)
        tvLowPriority = view.findViewById(R.id.tvLowPriority)

        setupClickListeners()
        observeData()
    }

    private fun setupClickListeners() {
        tvCompletedTasks.setOnClickListener {
            // Navigate to completed tasks using activity method
            (requireActivity() as? MainActivity)?.navigateToCompletedTasks()
        }
    }

    private fun observeData() {
        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            val total = tasks.size
            val completed = tasks.count { it.isCompleted }
            val pending = total - completed

            tvTotalTasks.text = total.toString()
            tvCompletedTasks.text = completed.toString()
            tvPendingTasks.text = pending.toString()

            val completionRate = if (total > 0) (completed * 100 / total) else 0
            progressCompletion.progress = completionRate

            // Make completed tasks count clickable - using ContextCompat for API 21+ compatibility
            if (completed > 0) {
                tvCompletedTasks.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_color))
                tvCompletedTasks.isClickable = true
            } else {
                tvCompletedTasks.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                tvCompletedTasks.isClickable = false
            }
        }

        viewModel.getPendingCountByPriority(3).observe(viewLifecycleOwner) { count ->
            tvHighPriority.text = "High: $count tasks"
        }

        viewModel.getPendingCountByPriority(2).observe(viewLifecycleOwner) { count ->
            tvMediumPriority.text = "Medium: $count tasks"
        }

        viewModel.getPendingCountByPriority(1).observe(viewLifecycleOwner) { count ->
            tvLowPriority.text = "Low: $count tasks"
        }
    }
}