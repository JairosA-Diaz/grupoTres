package com.example.grupotres.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.grupotres.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Criterio 1 HU 3.0: Verificar sesión persistente
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            navGraph.setStartDestination(R.id.homeFragment)
        } else {
            navGraph.setStartDestination(R.id.loginFragment)
        }

        navController.graph = navGraph
    }
}