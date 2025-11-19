package com.example.assignment3.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.assignment3.R
import com.example.assignment3.data.Priority
import com.example.assignment3.data.Task
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class AddTaskDialogFragment : DialogFragment() {

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerPriority: Spinner
    private lateinit var tvDueDate: TextView
    private lateinit var btnPickDate: Button
    private lateinit var btnClearDate: Button

    private val viewModel: TaskViewModel by viewModels(ownerProducer = { requireActivity() })

    private var editingTask: Task? = null
    private var dueDate: Long? = null

    companion object {
        private const val ARG_TASK_ID = "task_id"

        fun newInstance(taskId: Long? = null): AddTaskDialogFragment {
            val fragment = AddTaskDialogFragment()
            taskId?.let {
                val args = Bundle()
                args.putLong(ARG_TASK_ID, it)
                fragment.arguments = args
            }
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_add_task, null)
        setupViews(view)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle(if (editingTask != null) getString(R.string.edit_task) else getString(R.string.add_new_task))
            .setPositiveButton(if (editingTask != null) getString(R.string.update) else getString(R.string.add), null)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        dialog.setOnShowListener {
            loadTaskData()
        }

        return dialog
    }

    private fun setupViews(view: View) {
        etTitle = view.findViewById(R.id.etTitle)
        etDescription = view.findViewById(R.id.etDescription)
        spinnerPriority = view.findViewById(R.id.spinnerPriority)
        tvDueDate = view.findViewById(R.id.tvDueDate)
        btnPickDate = view.findViewById(R.id.btnPickDate)
        btnClearDate = view.findViewById(R.id.btnClearDate)

        // Setup priority spinner
        val priorities = Priority.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = adapter

        // Due date picker
        btnPickDate.setOnClickListener {
            showDatePicker()
        }

        btnClearDate.setOnClickListener {
            dueDate = null
            tvDueDate.text = getString(R.string.no_date_set)
        }

        // Override positive button to validate input
        val positiveButton = (dialog as? androidx.appcompat.app.AlertDialog)?.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
        positiveButton?.setOnClickListener {
            if (validateInput()) {
                saveTask()
                dismiss()
            }
        }
    }

    private fun loadTaskData() {
        val taskId = arguments?.getLong(ARG_TASK_ID)
        if (taskId != null) {
            lifecycleScope.launch {
                viewModel.allTasks.collect { tasks ->
                    tasks.find { it.id == taskId }?.let { task ->
                        editingTask = task
                        etTitle.setText(task.title)
                        etDescription.setText(task.description ?: "")
                        spinnerPriority.setSelection(task.priority.ordinal)
                        dueDate = task.dueDate

                        dueDate?.let {
                            val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                            tvDueDate.text = dateFormat.format(Date(it))
                        } ?: run {
                            tvDueDate.text = getString(R.string.no_date_set)
                        }
                    }
                }
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        dueDate?.let {
            calendar.time = Date(it)
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                dueDate = selectedCalendar.timeInMillis

                val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                tvDueDate.text = dateFormat.format(selectedCalendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun validateInput(): Boolean {
        val title = etTitle.text.toString().trim()
        if (title.isEmpty()) {
            etTitle.error = getString(R.string.title_required)
            return false
        }
        etTitle.error = null
        return true
    }

    private fun saveTask() {
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val priority = Priority.values()[spinnerPriority.selectedItemPosition]

        if (editingTask != null) {
            val updatedTask = editingTask!!.copy(
                title = title,
                description = if (description.isEmpty()) null else description,
                priority = priority,
                dueDate = dueDate
            )
            viewModel.updateTask(updatedTask)
        } else {
            viewModel.addTask(title, description, priority, dueDate)
        }
    }
}