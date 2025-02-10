package chu.monscout.kagamin.feature

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.java.KoinJavaComponent.get

@Composable
expect fun KagaminApp(
    kagaminViewModel: KagaminViewModel = get(KagaminViewModel::class.java),
    modifier: Modifier = Modifier
)