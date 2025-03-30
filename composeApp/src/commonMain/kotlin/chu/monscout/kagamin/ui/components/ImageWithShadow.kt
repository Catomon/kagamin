package chu.monscout.kagamin.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import chu.monscout.kagamin.Colors

@Composable
fun ImageWithShadow(
    painterResource: Painter,
    s: String,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Image(
            painterResource,
            contentDescription = s,
            colorFilter = ColorFilter.tint(Colors.currentYukiTheme.thinBorder),
            modifier = Modifier.graphicsLayer(translationY = 2f)
        )

        Image(
            painterResource,
            contentDescription = s,
            colorFilter = colorFilter
        )
    }
}