package com.amitsalunke.todolistapp.util

import androidx.appcompat.widget.SearchView

//extension function
inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    //below this refers to the current above mentioned searchView
    //object is used as an annomyous class
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }

    })
}