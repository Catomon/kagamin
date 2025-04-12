package chu.monscout.kagamin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chu.monscout.kagamin.ui.theme.Colors
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.add
import org.jetbrains.compose.resources.painterResource

@Composable
fun AddItemButton(onClick: () -> Unit, modifier: Modifier) {
    IconButton(onClick = onClick, modifier.background(Colors.bars, CircleShape).size(32.dp)) {
        Icon(painterResource(Res.drawable.add), "Add button", modifier = Modifier.size(16.dp))
    }
}