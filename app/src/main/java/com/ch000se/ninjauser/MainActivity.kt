package com.ch000se.ninjauser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.ch000se.ninjauser.presentation.navigation.NavGraph
import com.ch000se.ninjauser.ui.theme.NinjaUserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NinjaUserTheme {
                val navHostController = rememberNavController()
                NavGraph(
                    navHostController
                )
            }
        }
    }
}
