package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.star64
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppName(modifier: Modifier = Modifier,  height: Dp = 32.dp, fontSize: TextUnit = 18.sp) {
//    AppNameWShadow(modifier)
    AppNameWShadowJap(modifier, height, fontSize)
}

@Composable
private fun AppNameNormal(modifier: Modifier = Modifier, height: Dp = 32.dp, fontSize: TextUnit = 18.sp) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = "Kag",
            color = KagaminTheme.colors.buttonIcon,
            fontSize = fontSize,
            modifier = Modifier.height(height),
        )
        Image(
            painterResource(Res.drawable.star64),
            "App icon",
            colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon),
            modifier = Modifier.size(height).offset(y = (-3).dp)
        )
        Text(
            text = "min",
            color = KagaminTheme.colors.buttonIcon,
            fontSize = 18.sp,
            modifier = Modifier.height(height),
        )
    }
}

@Composable
private fun AppNameWShadowJap(modifier: Modifier = Modifier, height: Dp = 32.dp, fontSize: TextUnit = 18.sp) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.graphicsLayer(translationY = 3f)
        ) {
            Text(
                text = "かが",
                color = KagaminTheme.colors.thinBorder,
                fontSize = fontSize,
            )
            Image(
                painterResource(Res.drawable.star64),
                "App icon",
                colorFilter = ColorFilter.tint(KagaminTheme.colors.thinBorder),
                modifier = Modifier.size(height)
            )
            Text(
                text = "みん",
                color = KagaminTheme.colors.thinBorder,
                fontSize = fontSize,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
        ) {
            Text(
                text = "かが",
                color = KagaminTheme.colors.buttonIcon,
                fontSize = fontSize,
            )
            Image(
                painterResource(Res.drawable.star64),
                "App icon",
                colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon),
                modifier = Modifier.size(height)
            )
            Text(
                text = "みん",
                color = KagaminTheme.colors.buttonIcon,
                fontSize = fontSize,
            )
        }
    }
}

@Composable
private fun AppNameWShadow(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.graphicsLayer(translationY = 3f)
        ) {
            Text(
                text = "Kag",
                color = KagaminTheme.colors.thinBorder,
                fontSize = 18.sp,
            )
            Image(
                painterResource(Res.drawable.star64),
                "App icon",
                colorFilter = ColorFilter.tint(KagaminTheme.colors.thinBorder),
                modifier = Modifier.size(25.dp)
            )
            Text(
                text = "min",
                color = KagaminTheme.colors.thinBorder,
                fontSize = 18.sp,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
        ) {
            Text(
                text = "Kag",
                color = KagaminTheme.colors.buttonIcon,
                fontSize = 18.sp,
            )
            Image(
                painterResource(Res.drawable.star64),
                "App icon",
                colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon),
                modifier = Modifier.size(25.dp)
            )
            Text(
                text = "min",
                color = KagaminTheme.colors.buttonIcon,
                fontSize = 18.sp,
            )
        }
    }
}

@Composable
private fun AppNameOutlined(modifier: Modifier = Modifier) {
    Row(modifier) {
        OutlinedText(
            text = "Kag",
            fillColor = KagaminTheme.colors.buttonIcon,
            outlineColor = KagaminTheme.colors.thinBorder,
            fontSize = 18.sp,
            modifier = Modifier.height(32.dp),
            outlineDrawStyle = Stroke(4f)
        )
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(32.dp)) {
            Image(
                painterResource(Res.drawable.star64),
                "App icon",
                colorFilter = ColorFilter.tint(KagaminTheme.colors.thinBorder),
                modifier = Modifier.size(32.dp).offset(y = (-3).dp).graphicsLayer(
                    scaleX = 1.25f, scaleY = 1.25f
                )
            )
            Image(
                painterResource(Res.drawable.star64),
                "App icon",
                colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon),
                modifier = Modifier.size(30.dp).offset(y = (-3).dp)
            )
        }
        OutlinedText(
            text = "min",
            fillColor = KagaminTheme.colors.buttonIcon,
            outlineColor = KagaminTheme.colors.thinBorder,
            fontSize = 18.sp,
            modifier = Modifier.height(32.dp),
            outlineDrawStyle = Stroke(4f)
        )
    }
}

