package chu.monscout.kagamin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import chu.monscout.kagamin.Colors
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.lyrical_lily
import kagamin.composeapp.generated.resources.stars_background
import org.jetbrains.compose.resources.painterResource

@Composable
fun BackgroundImage() {
    Image(
        painterResource(Res.drawable.stars_background),
        "Background",
        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(Colors.currentYukiTheme.background2)
    )

//    Image(
//        painterResource(Res.drawable.lyrical_lily),
//        "Background",
//        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
//        contentScale = ContentScale.Crop,
//    )
}