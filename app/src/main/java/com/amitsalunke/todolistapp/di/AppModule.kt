package com.amitsalunke.todolistapp.di

import android.app.Application
import androidx.room.Room
import com.amitsalunke.todolistapp.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: TaskDatabase.Callback
    ) = Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    @Singleton
    fun provideTaskDao(
        db: TaskDatabase
    ) = db.taskDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
    //more effiecent then global scope it will leave till our application leaves ,can be use for long running application for our whole app
    //when two or more operations are running in one scope and if one of the operation fails all the operations in that scope gets fail so to avoid this we use SupervisorJob
}


//created for avoiding ambiguity
//creating our own annotation
@Retention(AnnotationRetention.RUNTIME)//visible for reflection
@Qualifier
annotation class ApplicationScope