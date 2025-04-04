package chu.monscout.kagamin.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import chu.monscout.kagamin.ui.screens.KagaminViewModel
import org.koin.java.KoinJavaComponent.get

@Composable
expect fun KagaminApp(
    kagaminViewModel: KagaminViewModel = get(KagaminViewModel::class.java),
    modifier: Modifier = Modifier
)