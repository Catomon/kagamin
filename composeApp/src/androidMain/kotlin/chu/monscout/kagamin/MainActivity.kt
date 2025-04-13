package chu.monscout.kagamin

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import chu.monscout.kagamin.ui.KagaminApp
import chu.monscout.kagamin.ui.theme.KagaminTheme
import com.github.catomon.yukinotes.di.appModule
import org.koin.core.context.GlobalContext.startKoin

var playerContext: (() -> Context)? = null

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            modules(appModule)
        }

        setContent {
            val context = this
            playerContext = { context }

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