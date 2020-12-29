package com.amitsalunke.todolistapp.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.amitsalunke.todolistapp.data.PreferencesManager
import com.amitsalunke.todolistapp.data.SortOrder
import com.amitsalunke.todolistapp.data.Task
import com.amitsalunke.todolistapp.data.TaskDao
import com.amitsalunke.todolistapp.ui.ADD_TASK_RESULT_OK
import com.amitsalunke.todolistapp.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {
    //we cant store flow in saved state handle but we can store live data and we can turn live data to flow and vice a versa so to convert live data to flow is to use .asFlow()
    //val searchQuery = MutableStateFlow("")
    val searchQuery =
        state.getLiveData("searchQuery", "") //"" is initial value as when start the app first time
    //there is no need of to set state which is in livedata for future search query

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
        searchQuery.asFlow(),
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        //as in task entity we have made each elements to be val and not var so hence we are using copy of task
        taskDao.update(task.copy(completed = isChecked))
    }

    //implementation of channel
    private val tasksEventChannel = Channel<TaskEvent>()

    val taskEvent = tasksEventChannel.receiveAsFlow()

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        //with help of channels we send data between two coroutines (is used to send events)
        tasksEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    //fab click
    fun onAddNewTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int) {
        //we are not writing (is) as because we are comparing the value
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")

        }
    }

    private fun showTaskSavedConfirmationMessage(msg: String) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.ShowTaskSavedConfirmationMessage(msg))
    }

    fun onDeleteAllCompletedClick() = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToDeleteAllCompletedScreen)
    }

    sealed class TaskEvent {
        object NavigateToAddTaskScreen :
            TaskEvent()//it is created as object class as we not sending any data and there will be only single instance of this class

        data class NavigateToEditTaskScreen(val task: Task) : TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TaskEvent()
        object NavigateToDeleteAllCompletedScreen : TaskEvent()
    }

}

