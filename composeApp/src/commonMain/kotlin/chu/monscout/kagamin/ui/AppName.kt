package chu.monscout.kagamin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.ui.components.OutlinedText
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.star64
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppName(modifier: Modifier = Modifier) {
//    AppNameWShadow(modifier)
    AppNameWShadowJap(modifier)
}

@Composable
private fun AppNameNormal(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = "Kag",
            color = Colors.currentYukiTheme.playerButtonIcon,
            fontSize = 18.sp,
            modifier = Modifier.height(32.dp),
        )
        Image(
            painterResource(Res.drawable.star64),
            "App icon",
            colorFilter = ColorFilter.tint(Colors.currentYukiTheme.playerButtonIcon),
            modifier = Modifier.size(32.dp).offset(y = (-3).dp)
        )
        Text(
            text = "min",
            color = Colors.currentYukiTheme.playerButtonIcon,
            fontSize = 18.sp,
            modifier = Modifier.height(32.dp),
        )
    }
}

@Composable
private fun AppNameWShadowJap(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.graphicsLayer(translationY = 3f)
        ) {
            Image(
                painterResource(Res.drawable.star64),
                "App icon",
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.thinBorder),
                modifier = Modifier.size(32.dp).offset(y = (-3).dp)
            )
            Text(
                text = "かがみん",
                color = Colors.currentYukiTheme.thinBorder,
                fontSize = 18.sp,
                modifier = Modifier.height(32.dp),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
        ) {
            Image(
                painterResource(Res.drawable.star64),
                "App icon",
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.playerButtonIcon),
                modifier = Modifier.size(32.dp).offset(y = (-3).dp)
            )
            Text(
                text = "かがみん",
                color = Colors.currentYukiTheme.playerButtonIcon,
                fontSize = 18.sp,
                modifier = Modifier.height(32.dp),
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
                color = Colors.currentYukiTheme.thinBorder,
                fontSize = 18.sp,
                modifier = Modifier.height(32.dp),
            )
            Image(
                painterResource(Res.drawable.star64),
                "App icon",
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.thinBorder),
                modifier = Modifier.size(32.dp).offset(y = (-3).dp)
            )
            Text(
                text = "min",
                color = Colors.currentYukiTheme.thinBorder,
                fontSize = 18.sp,
                modifier = Modifier.height(32.dp),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
        ) {
            Text(
                text = "Kag",
                color = Colors.currentYukiTheme.playerButtonIcon,
                fontSize = 18.sp,
                modifier = Modifier.height(32.dp),
            )
            Image(
                painterResource(Res.drawable.star64),
                "App icon",
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.playerButtonIcon),
                modifier = Modifier.size(32.dp).offset(y = (-3).dp)
            )
            Text(
                text = "min",
                color = Colors.currentYukiTheme.playerButtonIcon,
                fontSize = 18.sp,
                modifier = Modifier.height(32.dp),
            )
        }
    }
}

@Composable
private fun AppNameOutlined(modifier: Modifier = Modifier) {
    Row(modifier) {
        OutlinedText(
            text = "Kag",
            fillColor = Colors.currentYukiTheme.playerButtonIcon,
            outlineColor = Colors.currentYukiTheme.thinBorder,
            fontSize = 18.sp,
            modifier = Modifier.height(32.dp),
            outlineDrawStyle = Stroke(4f)
        )
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(32.dp)) {
            Image(
                painterResource(Res.drawable.star64),
                "App icon",
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.thinBorder),
                modifier = Modifier.size(32.dp).offset(y = (-3).dp).graphicsLayer(
                    scaleX = 1.25f, scaleY = 1.25f
                )
            )
            Image(
                painterResource(Res.drawable.star64),
                "App icon",
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.playerButtonIcon),
                modifier = Modifier.size(30.dp).offset(y = (-3).dp)
            )
        }
        OutlinedText(
            text = "min",
            fillColor = Colors.currentYukiTheme.playerButtonIcon,
            outlineColor = Colors.currentYukiTheme.thinBorder,
            fontSize = 18.sp,
            modifier = Modifier.height(32.dp),
            outlineDrawStyle = Stroke(4f)
        )
    }
}

