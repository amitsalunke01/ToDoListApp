package com.amitsalunke.todolistapp.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.amitsalunke.todolistapp.data.Task
import com.amitsalunke.todolistapp.data.TaskDao

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
}