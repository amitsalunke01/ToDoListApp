package com.amitsalunke.todolistapp.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.amitsalunke.todolistapp.data.PreferencesManager
import com.amitsalunke.todolistapp.data.SortOrder
import com.amitsalunke.todolistapp.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    //logic for sorting by name and date and hide completed
    /* val sortOrder = MutableStateFlow(SortOrder.BY_DATE)

     val hideCompleted = MutableStateFlow(false)*/

    val preferencesFlow = preferencesManager.preferencesFlow

/*
    //flatMapLatest is a flow operator so whenever we type search query it will execute the following block (it) is the current value of the search query (so basically we switch tracks in flow) (
    // same is available for live data and the operator is switch map)
    private val tasksFlow = searchQuery.flatMapLatest {
        taskDao.getTasks(it)
    }
    val tasks = tasksFlow.asLiveData()
*/

    //will combine multiple flow into one
    /*private val tasksFlow = combine(
        searchQuery,
        sortOrder,
        hideCompleted
    ) { query, sortOrder, hideCompleted ->
        Triple(query, sortOrder, hideCompleted)
    }.flatMapLatest { (query, sortOrder, hideCompleted) ->
        taskDao.getTasks(query, sortOrder, hideCompleted)
    }*/

    private val tasksFlow = combine(
        searchQuery,
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

}

