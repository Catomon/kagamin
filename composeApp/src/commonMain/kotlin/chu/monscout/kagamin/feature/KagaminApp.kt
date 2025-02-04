package chu.monscout.kagamin.feature

import LocalSnackbarHostState
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.java.KoinJavaComponent.get

@Composable
fun KagaminApp(
    kagaminViewModel: KagaminViewModel = get(KagaminViewModel::class.java),
    modifier: Modifier = Modifier
) {
    Scaffold(snackbarHost = { SnackbarHost(LocalSnackbarHostState.current) }, modifier = modifier) {
        MainScreen(kagaminViewModel)//, modifier)
    }
}