package com.amitsalunke.todolistapp.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.amitsalunke.todolistapp.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class TaskViewModel @ViewModelInject constructor(private val taskDao: TaskDao) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    //flatMapLatest is a flow operator so whenever we type search query it will execute the following block (it) is the current value of the search query (so basically we switch tracks in flow) (
    // same is available for live data and the operator is switch map)
    private val tasksFlow = searchQuery.flatMapLatest {
        taskDao.getTasks(it)
    }
    val tasks = tasksFlow.asLiveData()
}