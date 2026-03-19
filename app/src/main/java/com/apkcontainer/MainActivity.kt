package com.apkcontainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.apkcontainer.sandbox.SandboxManager
import com.apkcontainer.ui.navigation.AppNavGraph
import com.apkcontainer.ui.theme.ApkContainerTheme
import com.apkcontainer.ui.theme.ThemeMode
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sandboxManager: SandboxManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
            val navController = rememberNavController()

            ApkContainerTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(
                        navController = navController,
                        sandboxManager = sandboxManager,
                        onThemeChanged = { mode ->
                            themeMode = when (mode) {
                                "light" -> ThemeMode.LIGHT
                                "dark" -> ThemeMode.DARK
                                else -> ThemeMode.SYSTEM
                            }
                        }
                    )
                }
            }
        }
    }
}
