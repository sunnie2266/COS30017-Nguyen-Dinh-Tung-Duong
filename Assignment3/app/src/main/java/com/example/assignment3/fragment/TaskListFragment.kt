package com.example.assignment3.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.MainActivity
import com.example.assignment3.R
import com.example.assignment3.adapter.TaskAdapter
import com.example.assignment3.adapter.TaskGridAdapter
import com.example.assignment3.database.entity.Task
import com.example.assignment3.viewmodel.TaskViewModel

class TaskListFragment : Fragment() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddTask: Button
    private lateinit var btnCompletedTasks: Button
    private lateinit var radioGroupViewType: RadioGroup

    private lateinit var listAdapter: TaskAdapter
    private lateinit var gridAdapter: TaskGridAdapter
    private var isListView = true

    private var currentTasks: List<Task> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)

        recyclerView = view.findViewById(R.id.rvTasks)
        btnAddTask = view.findViewById(R.id.btnAddTask)
        radioGroupViewType = view.findViewById(R.id.radioGroupViewType)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[TaskViewModel::class.java]

        setupAdapters()
        setupRecyclerView()
        setupListeners()
        observeData()
    }

    private fun setupAdapters() {
        listAdapter = TaskAdapter(
            emptyList(),
            onTaskClick = { task ->
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
                showDeleteDialog(task)
            },
            onCheckboxClick = { task, isChecked ->
                val updatedTask = task.copy(isCompleted = isChecked)
                viewModel.updateTask(updatedTask)
            }
        )

        gridAdapter = TaskGridAdapter(
            emptyList(),
            onTaskClick = { task ->
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
            onCheckboxClick = { task, isChecked ->
                val updatedTask = task.copy(isCompleted = isChecked)
                viewModel.updateTask(updatedTask)
            }
        )
    }

    private fun setupRecyclerView() {
        if (isListView) {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = listAdapter
            listAdapter.updateTasks(currentTasks)
        } else {
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerView.adapter = gridAdapter
            gridAdapter.updateTasks(currentTasks)
        }
    }

    private fun setupListeners() {
        btnAddTask.setOnClickListener {
            val addTaskFragment = AddTaskFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, addTaskFragment)
                .addToBackStack(null)
                .commit()
        }

        radioGroupViewType.setOnCheckedChangeListener { _, checkedId ->
            isListView = checkedId == R.id.radioList
            setupRecyclerView()
        }
    }

    private fun observeData() {
        viewModel.pendingTasks.observe(viewLifecycleOwner) { tasks ->
            currentTasks = tasks
            if (isListView) {
                listAdapter.updateTasks(tasks)
            } else {
                gridAdapter.updateTasks(tasks)
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