package com.example.assignment3.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.assignment3.R
import com.example.assignment3.data.TaskRepository
import com.example.assignment3.data.TaskDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var fabAddTask: FloatingActionButton
    private val viewModel: TaskViewModel by viewModels {
        val repository = TaskRepository(TaskDatabase.getInstance(this).taskDao())
        TaskViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        setupBottomNavigation()
        setupFloatingActionButton()

        // Load initial fragment
        if (savedInstanceState == null) {
            replaceFragment(TaskListFragment())
        }
    }

    private fun initializeViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
        fabAddTask = findViewById(R.id.fab_add_task)
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_all -> {
                    replaceFragment(TaskListFragment())
                    true
                }
                R.id.navigation_active -> {
                    replaceFragment(ActiveTasksFragment())
                    true
                }
                R.id.navigation_completed -> {
                    replaceFragment(CompletedTasksFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun setupFloatingActionButton() {
        fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun showAddTaskDialog() {
        val dialog = AddTaskDialogFragment()
        dialog.show(supportFragmentManager, "AddTaskDialog")
    }

    fun getTaskViewModel(): TaskViewModel = viewModel
}