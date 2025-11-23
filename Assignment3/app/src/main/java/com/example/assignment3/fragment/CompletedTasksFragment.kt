package com.example.assignment3.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.R
import com.example.assignment3.adapter.TaskAdapter
import com.example.assignment3.database.entity.Task
import com.example.assignment3.viewmodel.TaskViewModel

class CompletedTasksFragment : Fragment() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_completed_tasks, container, false)

        recyclerView = view.findViewById(R.id.rvCompletedTasks)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[TaskViewModel::class.java]

        setupAdapter()
        setupRecyclerView()
        observeData()
    }

    private fun setupAdapter() {
        adapter = TaskAdapter(
            emptyList(),
            onTaskClick = { task ->
                // Navigate to task detail
                val detailFragment = TaskDetailFragment().apply {
                    arguments = Bundle().apply {
                        putLong("task_id", task.id)
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit()
            },
            onTaskLongClick = { task ->
                // Show delete dialog
                showDeleteDialog(task)
            },
            onCheckboxClick = { task, isChecked ->
                // Allow un-completing tasks
                val updatedTask = task.copy(isCompleted = isChecked)
                viewModel.updateTask(updatedTask)
            }
        )
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun observeData() {
        viewModel.completedTasks.observe(viewLifecycleOwner) { tasks ->
            if (tasks.isEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                tvEmptyState.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.updateTasks(tasks)
            }
        }
    }

    private fun showDeleteDialog(task: Task) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete '${task.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteTask(task)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}