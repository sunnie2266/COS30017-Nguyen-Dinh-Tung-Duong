package com.example.assignment3.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.assignment3.R
import com.example.assignment3.database.entity.Task
import com.example.assignment3.viewmodel.TaskViewModel
import java.util.*

class AddTaskFragment : Fragment() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var tvDueDate: TextView
    private lateinit var spinnerPriority: Spinner
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private var selectedDueDate: Date? = null
    private var editingTask: Task? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_task, container, false)

        etTitle = view.findViewById(R.id.etTitle)
        etDescription = view.findViewById(R.id.etDescription)
        tvDueDate = view.findViewById(R.id.tvDueDate)
        spinnerPriority = view.findViewById(R.id.spinnerPriority)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[TaskViewModel::class.java]

        setupSpinners()
        setupListeners()
        loadTaskForEditing()
    }

    private fun setupSpinners() {
        // Priority spinner
        val priorityOptions = arrayOf("Low", "Medium", "High")
        val priorityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorityOptions)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter

        // Category spinner
        val categoryOptions = arrayOf("Personal", "Work", "Study", "Health", "Other")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryOptions)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter
    }

    private fun setupListeners() {
        tvDueDate.setOnClickListener {
            showDatePicker()
        }

        btnSave.setOnClickListener {
            saveTask()
        }

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadTaskForEditing() {
        arguments?.getLong("task_id")?.let { taskId ->
            viewModel.getTaskById(taskId).observe(viewLifecycleOwner) { task ->
                task?.let {
                    editingTask = it
                    populateTaskData(it)
                }
            }
        }
    }

    private fun populateTaskData(task: Task) {
        etTitle.setText(task.title)
        etDescription.setText(task.description ?: "")

        // Set priority
        spinnerPriority.setSelection(task.priority - 1)

        // Set category
        val categoryAdapter = spinnerCategory.adapter as ArrayAdapter<String>
        val categoryPosition = categoryAdapter.getPosition(task.category)
        if (categoryPosition >= 0) {
            spinnerCategory.setSelection(categoryPosition)
        }

        // Set due date
        task.dueDate?.let { date ->
            selectedDueDate = date
            val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            tvDueDate.text = dateFormat.format(date)
        }

        // Update button text for editing
        btnSave.text = "Update Task"
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        selectedDueDate?.let { date ->
            calendar.time = date
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                selectedDueDate = selectedCalendar.time

                val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                tvDueDate.text = dateFormat.format(selectedDueDate!!)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun saveTask() {
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val priority = spinnerPriority.selectedItemPosition + 1
        val category = spinnerCategory.selectedItem.toString()

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }

        if (editingTask != null) {
            // Update existing task
            val updatedTask = editingTask!!.copy(
                title = title,
                description = if (description.isEmpty()) null else description,
                dueDate = selectedDueDate,
                priority = priority,
                category = category
            )
            viewModel.updateTask(updatedTask)
            Toast.makeText(requireContext(), "Task updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            // Create new task
            val task = Task(
                title = title,
                description = if (description.isEmpty()) null else description,
                dueDate = selectedDueDate,
                priority = priority,
                category = category
            )
            viewModel.insertTask(task)
            Toast.makeText(requireContext(), "Task created successfully", Toast.LENGTH_SHORT).show()
        }

        parentFragmentManager.popBackStack()
    }
}