package com.example.task1
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class UpdateActivity : AppCompatActivity() {

    private lateinit var edtTitle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var btnUpdate: Button
    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        edtTitle = findViewById(R.id.edtname)
        edtDescription = findViewById(R.id.edtnum)
        btnUpdate = findViewById(R.id.btn1)
        dbHelper = DbHelper(this)

        val taskId = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("desc") ?: ""

        edtTitle.setText(title)
        edtDescription.setText(description)

        btnUpdate.setOnClickListener {
            val updatedTitle = edtTitle.text.toString().trim()
            val updatedDescription = edtDescription.text.toString().trim()

            if (updatedTitle.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(this, "Please enter valid task details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedTask = TaskModel(
                id = taskId,
                title = updatedTitle,
                description = updatedDescription
            )

            dbHelper.updateTask(updatedTask)
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()

            finish() // Close activity and return to list
        }
    }
}