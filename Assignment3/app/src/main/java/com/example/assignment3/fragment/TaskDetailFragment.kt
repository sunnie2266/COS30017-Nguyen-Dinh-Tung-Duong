package com.example.assignment3.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.assignment3.R
import com.example.assignment3.database.entity.Task
import com.example.assignment3.util.Priority
import com.example.assignment3.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailFragment : Fragment() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var task: Task

    private lateinit var cbCompleted: CheckBox
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvDueDate: TextView
    private lateinit var tvPriority: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvCreated: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[TaskViewModel::class.java]

        initViews(view)
        setupListeners()
        observeTask()
    }

    private fun initViews(view: View) {
        cbCompleted = view.findViewById(R.id.cbDetailCompleted)
        tvTitle = view.findViewById(R.id.tvDetailTitle)
        tvDescription = view.findViewById(R.id.tvDetailDescription)
        tvDueDate = view.findViewById(R.id.tvDetailDueDate)
        tvPriority = view.findViewById(R.id.tvDetailPriority)
        tvCategory = view.findViewById(R.id.tvDetailCategory)
        tvCreated = view.findViewById(R.id.tvDetailCreated)
        btnEdit = view.findViewById(R.id.btnEdit)
        btnDelete = view.findViewById(R.id.btnDelete)
    }

    private fun setupListeners() {
        cbCompleted.setOnClickListener {
            val updatedTask = task.copy(isCompleted = cbCompleted.isChecked)
            viewModel.updateTask(updatedTask)
        }

        btnEdit.setOnClickListener {
            // Navigate to edit screen
            val editFragment = AddTaskFragment().apply {
                arguments = Bundle().apply {
                    putLong("task_id", task.id)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack(null)
                .commit()
        }

        btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun observeTask() {
        val taskId = arguments?.getLong("task_id") ?: -1L

        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            tasks.find { it.id == taskId }?.let { currentTask ->
                task = currentTask
                updateUI(currentTask)
            }
        }
    }

    private fun updateUI(task: Task) {
        tvTitle.text = task.title
        tvDescription.text = task.description ?: "No description"

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        tvDueDate.text = task.dueDate?.let { dateFormat.format(it) } ?: "No due date"

        tvPriority.text = Priority.getPriorityName(task.priority)

        // Set priority color
        val priorityColor = when (task.priority) {
            Priority.HIGH -> ContextCompat.getColor(requireContext(), R.color.priority_high)
            Priority.MEDIUM -> ContextCompat.getColor(requireContext(), R.color.priority_medium)
            else -> ContextCompat.getColor(requireContext(), R.color.priority_low)
        }
        tvPriority.setTextColor(priorityColor)

        tvCategory.text = task.category
        tvCreated.text = dateFormat.format(task.createdAt)
        cbCompleted.isChecked = task.isCompleted
    }

    private fun showDeleteConfirmation() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete '${task.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteTask(task)
                parentFragmentManager.popBackStack()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}