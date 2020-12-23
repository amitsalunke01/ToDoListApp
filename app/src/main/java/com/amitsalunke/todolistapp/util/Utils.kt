package com.amitsalunke.todolistapp.util


//extensive property it used to get compile time error in when clause if we dont implement all the cases
val <T> T.exhaustive: T
    get() = this