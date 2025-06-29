package com.example.task1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.task1.DbHelper
import com.example.task1.R
import com.example.task1.TaskModel

class MainActivity : AppCompatActivity() {

    private lateinit var edtTitle: EditText
    private lateinit var edtDesc: EditText
    private lateinit var btnAddTask: Button
    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = DbHelper(applicationContext)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        edtTitle = findViewById(R.id.edt1)
        edtDesc = findViewById(R.id.edt2)
        btnAddTask = findViewById(R.id.btnTask)

        loadTasksFragment()

        btnAddTask.setOnClickListener {
            val title = edtTitle.text.toString().trim()
            val description = edtDesc.text.toString().trim()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                val task = TaskModel(title = title, description = description)
                val result = dbHelper.insertTask(task)

                if (result > 0) {
                    Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
                    edtTitle.text.clear()
                    edtDesc.text.clear()
                    loadTasksFragment()
                } else {
                    Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter both title and description", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTasksFragment() {
        val fragment = DesignFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl, fragment)
            .commit()
    }
}
