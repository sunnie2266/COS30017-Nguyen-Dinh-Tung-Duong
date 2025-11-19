package com.example.assignment3.ui

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.R
import com.example.assignment3.data.Task
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {

    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var emptyState: TextView
    private val viewModel: TaskViewModel by viewModels(ownerProducer = { requireActivity() })
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewTasks = view.findViewById(R.id.recyclerView_tasks)
        emptyState = view.findViewById(R.id.empty_state)

        setupRecyclerView()
        observeTasks()
        setupSwipeToDelete()
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            onTaskClick = { task ->
                showEditTaskDialog(task)
            },
            onTaskToggle = { task ->
                viewModel.toggleTaskCompletion(task)
            },
            onTaskDelete = { task ->
                showDeleteConfirmation(task)
            }
        )

        recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TaskListFragment.adapter
        }
    }

    private fun observeTasks() {
        lifecycleScope.launch {
            viewModel.allTasks.collect { tasks ->
                adapter.submitList(tasks)
                emptyState.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupSwipeToDelete() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = adapter.currentList[position]
                viewModel.deleteTask(task)
            }
        }).attachToRecyclerView(recyclerViewTasks)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_task_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                viewModel.toggleSortOrder()
                true
            }
            R.id.action_clear_completed -> {
                viewModel.clearCompletedTasks()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showEditTaskDialog(task: Task) {
        val dialog = AddTaskDialogFragment.newInstance(task.id)
        dialog.show(parentFragmentManager, "EditTaskDialog")
    }

    private fun showDeleteConfirmation(task: Task) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_task))
            .setMessage(getString(R.string.delete_confirmation, task.title))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.deleteTask(task)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
}