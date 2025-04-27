package com.github.catomon.kagamin

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.github.catomon.kagamin.ui.KagaminApp
import com.github.catomon.kagamin.ui.theme.Colors
import com.github.catomon.kagamin.ui.theme.KagaminTheme

var playerContext: (() -> Context)? = null

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = Colors.theme.background.toArgb()

        val context = this
        playerContext = { context }

        setContent {
            KagaminTheme {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

@Composable
fun App() {
    KagaminApp()
}