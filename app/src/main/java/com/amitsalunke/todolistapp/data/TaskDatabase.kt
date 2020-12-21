package com.amitsalunke.todolistapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.amitsalunke.todolistapp.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>, //provider gives dependency lazy manner when we go to db operation code the database object would be created i.e in onCreate
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        //executed when we first time create the db
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            //db operation
            database.get()

            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("Wash the dishes"))
                dao.insert(Task("Work on kotlin", important = true))
                dao.insert(Task("Repair bike", completed = true))
            }
        }
    }
}
//generally below viewModel people implement flow and in view Model they implement live data
//difference between Flow data and liveData
//LiveData - has the single latest value and not the whole stream of values like flow, it is lifecycle aware so lifecycle activity is handle is very well eg when the fragment goes into background
//and becomes inactive, livedata will detect this and completely stop dispatching the event to the fragment
//Flow - is used below the viewModel because u can transform the value as it passes whole stream, we can switch the threads in flow and  we cannot lose any data as the whole stream