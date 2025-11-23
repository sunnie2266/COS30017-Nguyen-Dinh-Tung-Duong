package com.example.assignment3.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.R
import com.example.assignment3.database.entity.Task
import com.example.assignment3.util.Priority
import java.text.SimpleDateFormat
import java.util.*

class TaskGridAdapter(
    private var tasks: List<Task>,
    private val onTaskClick: (Task) -> Unit,
    private val onCheckboxClick: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskGridAdapter.TaskGridViewHolder>() {

    inner class TaskGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvGridTaskTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tvGridDescription)
        private val dueDateTextView: TextView = itemView.findViewById(R.id.tvGridDueDate)
        private val categoryTextView: TextView = itemView.findViewById(R.id.tvGridCategory)
        private val priorityIndicator: View = itemView.findViewById(R.id.viewPriorityIndicator)
        private val checkBox: CheckBox = itemView.findViewById(R.id.cbGridCompleted)

        fun bind(task: Task) {
            titleTextView.text = task.title
            descriptionTextView.text = task.description ?: "No description"

            // Format due date - task.dueDate is Long? (timestamp)
            dueDateTextView.text = formatDueDate(task.dueDate?.time)

            // Set priority indicator color
            val priorityColor = when (task.priority) {
                Priority.HIGH -> ContextCompat.getColor(itemView.context, R.color.priority_high)
                Priority.MEDIUM -> ContextCompat.getColor(itemView.context, R.color.priority_medium)
                Priority.LOW -> ContextCompat.getColor(itemView.context, R.color.priority_low)
                else -> ContextCompat.getColor(itemView.context, R.color.priority_low)
            }
            priorityIndicator.setBackgroundColor(priorityColor)

            // Set category
            categoryTextView.text = task.category

            // Set checkbox state
            checkBox.isChecked = task.isCompleted

            // Add strikethrough for completed tasks
            if (task.isCompleted) {
                titleTextView.paintFlags = titleTextView.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                descriptionTextView.paintFlags = descriptionTextView.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                dueDateTextView.paintFlags = dueDateTextView.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                categoryTextView.paintFlags = categoryTextView.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                itemView.alpha = 0.6f
            } else {
                titleTextView.paintFlags = titleTextView.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                descriptionTextView.paintFlags = descriptionTextView.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                dueDateTextView.paintFlags = dueDateTextView.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                categoryTextView.paintFlags = categoryTextView.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                itemView.alpha = 1.0f
            }

            itemView.setOnClickListener {
                onTaskClick(task)
            }

            checkBox.setOnClickListener {
                onCheckboxClick(task, checkBox.isChecked)
            }
        }

        private fun formatDueDate(dueDate: Long?): String {
            return if (dueDate == null) {
                "No date"
            } else {
                when {
                    isToday(dueDate) -> "Today"
                    isTomorrow(dueDate) -> "Tomorrow"
                    isYesterday(dueDate) -> "Yesterday"
                    else -> {
                        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                        dateFormat.format(Date(dueDate))
                    }
                }
            }
        }

        private fun isToday(timestamp: Long): Boolean {
            val today = Calendar.getInstance()
            val taskDate = Calendar.getInstance().apply { timeInMillis = timestamp }
            return today.get(Calendar.YEAR) == taskDate.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == taskDate.get(Calendar.DAY_OF_YEAR)
        }

        private fun isTomorrow(timestamp: Long): Boolean {
            val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
            val taskDate = Calendar.getInstance().apply { timeInMillis = timestamp }
            return tomorrow.get(Calendar.YEAR) == taskDate.get(Calendar.YEAR) &&
                    tomorrow.get(Calendar.DAY_OF_YEAR) == taskDate.get(Calendar.DAY_OF_YEAR)
        }

        private fun isYesterday(timestamp: Long): Boolean {
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            val taskDate = Calendar.getInstance().apply { timeInMillis = timestamp }
            return yesterday.get(Calendar.YEAR) == taskDate.get(Calendar.YEAR) &&
                    yesterday.get(Calendar.DAY_OF_YEAR) == taskDate.get(Calendar.DAY_OF_YEAR)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskGridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_grid, parent, false)
        return TaskGridViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskGridViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}