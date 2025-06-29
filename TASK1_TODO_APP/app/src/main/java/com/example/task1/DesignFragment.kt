package com.example.task1

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class DesignFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var taskList: MutableList<TaskModel>
    private lateinit var dbHelper: DbHelper

    private lateinit var adapter: SimpleAdapter
    private val displayList = ArrayList<HashMap<String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_design, container, false)
        dbHelper = DbHelper(requireContext())
        listView = view.findViewById(R.id.list)

        loadTasks()

        // Long click handler replaces context menu
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val selectedTask = taskList[position]

            AlertDialog.Builder(requireContext())
                .setTitle("Task Options")
                .setItems(arrayOf("Update", "Delete")) { _, which ->
                    when (which) {
                        0 -> {
                            val intent = Intent(requireContext(), UpdateActivity::class.java).apply {
                                putExtra("id", selectedTask.id)
                                putExtra("title", selectedTask.title)
                                putExtra("desc", selectedTask.description)
                            }
                            startActivity(intent)
                        }
                        1 -> {
                            dbHelper.deleteTask(selectedTask.id)
                            loadTasks()
                            Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .show()
            true
        }

        return view
    }

    private fun loadTasks() {
        taskList = dbHelper.getAllTasks()
        displayList.clear()

        for (task in taskList) {
            val taskMap = HashMap<String, String>()
            taskMap["title"] = task.title
            taskMap["description"] = task.description
            displayList.add(taskMap)
        }

        val from = arrayOf("title", "description")
        val to = intArrayOf(R.id.txtname, R.id.txtdisc)

        adapter = SimpleAdapter(requireContext(), displayList, R.layout.design, from, to)
        listView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadTasks() // Refresh task list when returning to this fragment
    }
}
