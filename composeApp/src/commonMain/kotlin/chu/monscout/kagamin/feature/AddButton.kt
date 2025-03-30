package chu.monscout.kagamin.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import chu.monscout.kagamin.Colors
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.add
import org.jetbrains.compose.resources.painterResource

@Composable
fun AddButton(painterResource: Painter = painterResource(Res.drawable.add), onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.padding(4.dp).size(32.dp).background(
            remember { Colors.currentYukiTheme.thinBorder.copy(alpha = 0.5f) },
            shape = CircleShape
        ).graphicsLayer(translationY = -2f)
    ) {
        TextButton(
            shape = CircleShape,
            modifier = Modifier.background(color = Colors.barsTransparent, shape = CircleShape)
                .clip(
                    CircleShape
                ),
            onClick = onClick
        ) {
            ImageWithShadow(
                painterResource,
                "Add button",
                modifier = Modifier.size(16.dp).graphicsLayer(scaleX = 1.25f, scaleY = 1.25f),
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.smallButtonIcon)
            )
        }
    }
}