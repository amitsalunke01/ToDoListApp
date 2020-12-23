package com.amitsalunke.todolistapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.amitsalunke.todolistapp.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment //casted to nav host fragment

        navController = navHostFragment.findNavController()

        //to handle back button to each of the fragment
        setupActionBarWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {//for up botton
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}