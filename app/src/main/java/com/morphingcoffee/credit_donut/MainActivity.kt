package com.morphingcoffee.credit_donut

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Main activity in the single-activity architecture.
 * Unless necessary to expand activity, prefer adding new functionality to fragment level.
 **/
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}