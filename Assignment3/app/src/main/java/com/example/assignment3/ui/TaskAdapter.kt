package com.example.assignment3.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.R
import com.example.assignment3.data.Task
import com.example.assignment3.data.Priority
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskToggle: (Task) -> Unit,
    private val onTaskDelete: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvDueDate: TextView = itemView.findViewById(R.id.tvDueDate)
        private val priorityIndicator: View = itemView.findViewById(R.id.priorityIndicator)
        private val cbCompleted: CheckBox = itemView.findViewById(R.id.cbCompleted)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(task: Task) {
            // Title with strike-through for completed tasks
            tvTitle.text = task.title
            if (task.isCompleted) {
                tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvTitle.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
            } else {
                tvTitle.paintFlags = tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvTitle.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))
            }

            // Description
            tvDescription.text = task.description
            tvDescription.visibility = if (task.description.isNullOrEmpty()) View.GONE else View.VISIBLE

            // Due date
            task.dueDate?.let {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                tvDueDate.text = dateFormat.format(Date(it))
                tvDueDate.visibility = View.VISIBLE
            } ?: run {
                tvDueDate.visibility = View.GONE
            }

            // Priority indicator
            val priorityColor = when (task.priority) {
                Priority.HIGH -> ContextCompat.getColor(itemView.context, android.R.color.holo_red_light)
                Priority.MEDIUM -> ContextCompat.getColor(itemView.context, android.R.color.holo_orange_light)
                Priority.LOW -> ContextCompat.getColor(itemView.context, android.R.color.holo_green_light)
            }
            priorityIndicator.setBackgroundColor(priorityColor)

            // Checkbox
            cbCompleted.isChecked = task.isCompleted
            cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != task.isCompleted) {
                    onTaskToggle(task)
                }
            }

            // Click listener for editing
            itemView.setOnClickListener {
                onTaskClick(task)
            }

            // Delete button
            btnDelete.setOnClickListener {
                onTaskDelete(task)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}