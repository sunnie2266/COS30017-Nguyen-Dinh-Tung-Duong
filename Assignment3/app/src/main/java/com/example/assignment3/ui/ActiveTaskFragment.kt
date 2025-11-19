package com.example.assignment3.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.R
import com.example.assignment3.data.Task
import kotlinx.coroutines.launch

class ActiveTasksFragment : Fragment() {

    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var emptyState: TextView
    private val viewModel: TaskViewModel by viewModels(ownerProducer = { requireActivity() })
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewTasks = view.findViewById(R.id.recyclerView_tasks)
        emptyState = view.findViewById(R.id.empty_state)

        setupRecyclerView()
        observeTasks()
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            onTaskClick = { task -> showEditTaskDialog(task) },
            onTaskToggle = { task -> viewModel.toggleTaskCompletion(task) },
            onTaskDelete = { task -> viewModel.deleteTask(task) }
        )

        recyclerViewTasks.adapter = adapter
        recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeTasks() {
        lifecycleScope.launch {
            viewModel.activeTasks.collect { tasks ->
                adapter.submitList(tasks)
                emptyState.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showEditTaskDialog(task: Task) {
        val dialog = AddTaskDialogFragment.newInstance(task.id)
        dialog.show(parentFragmentManager, "EditTaskDialog")
    }
}