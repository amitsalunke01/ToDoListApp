package com.amitsalunke.todolistapp.data

import androidx.room.*
import com.amitsalunke.todolistapp.ui.tasks.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    //|| append operator in sql lite  // if hide completed is true show all uncompleted task and vice versa but when it is false we need to show all data with completed and non completed is done by = 0 i.e is false
    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND  name LIKE '%' || :searchQuery || '%' ORDER BY important DESC,name")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND  name LIKE '%' || :searchQuery || '%' ORDER BY important DESC,created")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>


    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean) : Flow<List<Task>> =
        when(sortOrder){
            SortOrder.BY_DATE -> getTasksSortedByDateCreated(query,hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(query,hideCompleted)
        }
}

