package com.github.catomon.kagamin.ui.v3.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.ui.v3.theme.V3Theme
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.search
import org.jetbrains.compose.resources.painterResource

@Composable
fun V3PaneHeader(
    title: String,
    modifier: Modifier = Modifier,
    count: Int? = null,
    searching: Boolean = false,
    searchText: String = "",
    onSearchTextChange: (String) -> Unit = {},
    onToggleSearch: () -> Unit = {},
    actionIcon: Painter? = null,
    actionDescription: String = "",
    onAction: () -> Unit = {},
    leadingIcon: Painter? = null,
    leadingDescription: String = "",
    onTitleClick: (() -> Unit)? = null,
) {
    val colors = V3Theme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        if (searching) {
            BasicTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = colors.panelContent,
                    fontSize = 12.sp,
                ),
                cursorBrush = SolidColor(colors.accentSoft),
            )
        } else {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .then(
                        if (onTitleClick != null)
                            Modifier.clickable(onClickLabel = leadingDescription.ifBlank { title }, onClick = onTitleClick)
                        else Modifier
                    )
                    .padding(horizontal = 2.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leadingIcon != null) {
                    V3Icon(
                        painter = leadingIcon,
                        tint = colors.panelContentSecondary,
                        size = 15.dp,
                    )
                    Spacer(Modifier.width(4.dp))
                }

                V3OutlinedText(
                    text = title,
                    fillColor = colors.panelContent,
                    outlineColor = colors.headerOutline,
                    fontSize = 14.sp,
                    outlineSize = 3.5f,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                )

                if (count != null) {
                    V3OutlinedText(
                        text = " $count",
                        fillColor = colors.panelContentSecondary,
                        outlineColor = colors.headerOutline,
                        fontSize = 11.sp,
                        outlineSize = 3f,
                        maxLines = 1,
                    )
                }
            }

            Spacer(Modifier.weight(1f))
        }

        if (actionIcon != null) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClickLabel = actionDescription, onClick = onAction),
                contentAlignment = Alignment.Center,
            ) {
                V3Icon(
                    painter = actionIcon,
                    tint = colors.panelContentSecondary,
                    size = 16.dp,
                )
            }
        } else {
            Spacer(Modifier.width(28.dp))
        }

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClickLabel = "Search", onClick = onToggleSearch),
            contentAlignment = Alignment.Center,
        ) {
            V3Icon(
                painter = painterResource(Res.drawable.search),
                tint = if (searching) colors.accentSoft else colors.panelContentSecondary,
                size = 16.dp,
            )
        }
    }
}
