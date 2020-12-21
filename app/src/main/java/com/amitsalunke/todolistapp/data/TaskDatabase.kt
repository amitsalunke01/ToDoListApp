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