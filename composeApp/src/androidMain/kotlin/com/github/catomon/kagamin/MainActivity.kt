package com.github.catomon.kagamin

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.toArgb
import com.github.catomon.kagamin.ui.theme.KagaminTheme

var playerContext: (() -> Context)? = null

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = KagaminTheme.colors.background.toArgb()

        val context = this
        playerContext = { context }

        setContent {
            KagaminTheme {

            }
        }
    }
}
