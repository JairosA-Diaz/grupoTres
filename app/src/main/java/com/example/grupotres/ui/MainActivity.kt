package com.example.grupotres.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.grupotres.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // El FragmentContainerView en activity_main.xml se encarga de cargar el NavGraph
    }
}