package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.bg
import org.jetbrains.compose.resources.painterResource

@Composable
fun BackgroundImage(alpha: Float = DefaultAlpha) {
    Image(
        painterResource(Res.drawable.bg),
        "Background",
        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)).blur(5.dp),
        contentScale = ContentScale.Crop,
//        colorFilter = ColorFilter.tint(Colors.currentYukiTheme.background2),
        alpha = alpha
    )

//    Image(
//        painterResource(Res.drawable.stars_background),
//        "Background",
//        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
//        contentScale = ContentScale.Crop,
//        colorFilter = ColorFilter.tint(Colors.currentYukiTheme.background2),
//        alpha = alpha
//    )

//    Image(
//        painterResource(Res.drawable.lyrical_lily),
//        "Background",
//        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
//        contentScale = ContentScale.Crop,
//    )
}