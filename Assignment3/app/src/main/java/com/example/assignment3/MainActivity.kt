package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.assignment3.activity.LoginActivity
import com.example.assignment3.fragment.TaskListFragment
import com.example.assignment3.fragment.CompletedTasksFragment
import com.example.assignment3.fragment.AnalyticsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        initViews()
        setupBottomNavigation()

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(TaskListFragment())
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Task Manager"
    }

    private fun initViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_tasks -> {
                    loadFragment(TaskListFragment())
                    true
                }
                R.id.navigation_completed -> {
                    loadFragment(CompletedTasksFragment())
                    true
                }
                R.id.navigation_analytics -> {
                    loadFragment(AnalyticsFragment())
                    true
                }
                else -> false
            }
        }

        // Set initial selection
        bottomNavigation.selectedItemId = R.id.navigation_tasks
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Public method to allow fragments to change navigation
    fun navigateToCompletedTasks() {
        bottomNavigation.selectedItemId = R.id.navigation_completed
    }

    // Method to navigate to add task
    fun navigateToAddTask() {
        // You can implement this if you want to switch to AddTaskFragment
        // loadFragment(AddTaskFragment())
    }

    // Method to navigate to task detail
    fun navigateToTaskDetail(taskId: Long) {
        // You can implement this if you want to switch to TaskDetailFragment
        // val fragment = TaskDetailFragment.newInstance(taskId)
        // loadFragment(fragment)
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}