package com.example.task1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "todo.db"
        const val TABLE_NAME = "tasks"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createQuery = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT,
                $COL_DESCRIPTION TEXT
            )
        """.trimIndent()
        db.execSQL(createQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertTask(task: TaskModel): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TITLE, task.title)
            put(COL_DESCRIPTION, task.description)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllTasks(): ArrayList<TaskModel> {
        val taskList = ArrayList<TaskModel>()
        val db = readableDatabase

        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "$COL_ID DESC")

        cursor.use {
            while (it.moveToNext()) {
                val task = TaskModel(
                    id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COL_TITLE)),
                    description = it.getString(it.getColumnIndexOrThrow(COL_DESCRIPTION))
                )
                taskList.add(task)
            }
        }
        return taskList
    }

    fun deleteTask(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id.toString()))
    }

    fun updateTask(task: TaskModel): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TITLE, task.title)
            put(COL_DESCRIPTION, task.description)
        }
        return db.update(TABLE_NAME, values, "$COL_ID = ?", arrayOf(task.id.toString()))
    }
}