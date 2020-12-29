package com.amitsalunke.todolistapp.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amitsalunke.todolistapp.data.Task
import com.amitsalunke.todolistapp.data.TaskDao
import com.amitsalunke.todolistapp.ui.ADD_TASK_RESULT_OK
import com.amitsalunke.todolistapp.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle //when we us nav arg i.e navigation comp the data which is sent from 1 fragment to connecting fragment is stored automatically in SavedStateHandle
) : ViewModel() {
    val task =
        state.get<Task>("task")//key name have to be exact same which is stated in nav graph for this fragment

    //we getting value from saved instance i.e state , if it is not available then from task object if not present then empty string
    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {//to store the saved instance
            field = value //value is the input in the variable i.e taskName
            state.set("taskName", value) //storing data in saved state
        }

    var taskImportance =
        state.get<Boolean>("taskImportance") ?: task?.important
        ?: false //false is the default input
        set(value) {//to store the saved instance
            field = value //value is the input in the variable i.e taskName
            state.set("taskImportance", value) //storing data in saved state
        }

    //adding channel
    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()
    fun onSaveClick() {
        //checks for completely empty and black spaces
        if (taskName.isBlank()) {
            //invalid input msg
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        if (task != null) {
            val updatedTask = task.copy(name = taskName, important = taskImportance)
            updatedTask(updatedTask)
        } else {
            val newTask = Task(name = taskName, important = taskImportance)
            createTask(newTask)
        }
    }

    private fun updatedTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigationBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun createTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigationBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage(msg: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInValidInputMessage(msg))
    }

    sealed class AddEditTaskEvent {
        data class ShowInValidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigationBackWithResult(val result: Int) : AddEditTaskEvent()
    }
}