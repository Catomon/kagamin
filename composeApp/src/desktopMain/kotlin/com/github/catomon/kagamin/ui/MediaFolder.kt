package com.github.catomon.kagamin.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferAction
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.DragAndDropTransferable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.data.defaultMediaFolder
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.arrow_left
import kagamin.composeapp.generated.resources.folder
import kagamin.composeapp.generated.resources.note_icon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import java.awt.Desktop
import java.awt.datatransfer.StringSelection
import java.io.File

@Composable
fun MediaFolder(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    val mediaFolder = remember(viewModel.settings) {
        File(viewModel.settings.mediaFolderPath).let {
            if (it.exists()) it else defaultMediaFolder.also { it.mkdirs() }
        }
    }

    var currentFolder by remember { mutableStateOf(mediaFolder) }
    var loadingFolder by remember { mutableStateOf(currentFolder) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier
            .background(color = KagaminTheme.colors.backgroundTransparent)
            .padding(start = 4.dp),
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.height(32.dp).fillMaxWidth().padding(vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp)).clickable {
                    Desktop.getDesktop().open(currentFolder)
                }
        ) {
            Text(
                currentFolder.nameWithoutExtension,
                fontSize = 10.sp,
                color = KagaminTheme.colors.buttonIcon,
                modifier = Modifier,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (loadingFolder != mediaFolder)
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clickable {
                        currentFolder = currentFolder.parentFile ?: return@clickable
                    }
                    .padding(8.dp),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_left),
                    contentDescription = "Go Back",

                    tint = KagaminTheme.colors.buttonIcon,
                )
            }

        var files: List<File> by remember {
            mutableStateOf(emptyList())
        }

        LaunchedEffect(currentFolder) {
            files = withContext(Dispatchers.Default) {
                (currentFolder.listFiles()?.toList()?.sortedBy { if (it.isFile) 1 else 0 }
                    ?: throw IllegalArgumentException("Is not a folder")).filter { it.extension in audioExtensions || it.isDirectory }
            }

            loadingFolder = currentFolder
        }

        Folder(
            files = files,
            goToFolder = {
                currentFolder = it
            },
            openFile = { file ->
                coroutineScope.launch {
                    val track = viewModel.loadTracks(file.absolutePath)
                    track.firstOrNull()?.let { track ->
                        viewModel.play(track)
                    }
                }
            }
        )
    }
}

val audioExtensions = listOf(
    "mp3", "wav", "flac", "aac", "ogg", "m4a", "wma", "alac", "aiff", "opus"
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Folder(
    files: List<File>,
    goToFolder: (File) -> Unit,
    openFile: (File) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(state = listState, modifier = modifier.fillMaxWidth()) {
        if (files.isEmpty())
            item {
                Text(
                    "Empty",
                    color = KagaminTheme.colors.buttonIcon,
                    maxLines = 1,
                    fontSize = 12.sp
                )
            }
        else
            items(files.size, { files[it].path }) {
                val file = files[it]
                FolderItem(file, Modifier.fillMaxWidth().clickable {
                    if (file.isDirectory) goToFolder(file)
                    else openFile(file)
                }.dragAndDropSource { _ ->
                    val data = "tracks:" + (if (file.isDirectory) file.listFiles()
                        .joinToString("/") { it.absolutePath } else file.absolutePath)

                    DragAndDropTransferData(
                        transferable = DragAndDropTransferable(StringSelection(data)),
                        supportedActions = listOf(DragAndDropTransferAction.Copy),
                    )
                })
            }
    }
}

@Composable
fun FolderItem(file: File, modifier: Modifier = Modifier) {
    Row(
        modifier.defaultMinSize(minHeight = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(if (file.isDirectory) Res.drawable.folder else Res.drawable.note_icon),
            null,
            tint = KagaminTheme.colors.buttonIcon,
            modifier = Modifier.size(20.dp)
        )

        Text(
            if (file.isDirectory) file.name else file.nameWithoutExtension,
            fontSize = 10.sp,
            //color = KagaminTheme.colors.buttonIcon,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 15.sp,
            modifier = Modifier.padding(vertical = 3.dp)
        )
    }
}