package com.amitsalunke.todolistapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

//for room database
@Entity(tableName = "task_table")
// @Parcelize for sending data from one fragment to other fragment
@Parcelize
data class Task(
    val name: String,
    val important: Boolean = false,
    val completed: Boolean = false,
    val created: Long = System.currentTimeMillis(), //automatically set time in millisec
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) : Parcelable {
    //formatted time to be shown, we will use this so to dynamically show the formatted one
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)
}