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
import java.util.Locale
import android.graphics.Paint

class TaskAdapter(
    private var tasks: List<Task>,
    private val onTaskClick: (Task) -> Unit,
    private val onTaskLongClick: (Task) -> Unit,
    private val onCheckboxClick: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvTaskTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tvTaskDescription)
        private val dueDateTextView: TextView = itemView.findViewById(R.id.tvDueDate)
        private val priorityTextView: TextView = itemView.findViewById(R.id.tvPriority)
        private val categoryTextView: TextView = itemView.findViewById(R.id.tvCategory)
        private val checkBox: CheckBox = itemView.findViewById(R.id.cbCompleted)

        fun bind(task: Task) {
            titleTextView.text = task.title
            descriptionTextView.text = task.description ?: "No description"

            if (task.isCompleted) {
                titleTextView.paintFlags = titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                descriptionTextView.paintFlags = descriptionTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                titleTextView.paintFlags = titleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                descriptionTextView.paintFlags = descriptionTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            dueDateTextView.text = task.dueDate?.let { dateFormat.format(it) } ?: "No due date"

            priorityTextView.text = Priority.getPriorityName(task.priority)

            // Set priority text color
            val priorityColor = when (task.priority) {
                Priority.HIGH -> ContextCompat.getColor(itemView.context, R.color.priority_high)
                Priority.MEDIUM -> ContextCompat.getColor(itemView.context, R.color.priority_medium)
                else -> ContextCompat.getColor(itemView.context, R.color.priority_low)
            }
            priorityTextView.setTextColor(priorityColor)

            categoryTextView.text = task.category
            checkBox.isChecked = task.isCompleted

            itemView.setOnClickListener {
                onTaskClick(task)
            }

            itemView.setOnLongClickListener {
                onTaskLongClick(task)
                true
            }

            checkBox.setOnClickListener {
                onCheckboxClick(task, checkBox.isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}