package com.amitsalunke.todolistapp.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.amitsalunke.todolistapp.data.TaskDao

class TaskViewModel @ViewModelInject constructor(private val taskDao: TaskDao) : ViewModel() {
}