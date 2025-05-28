package com.github.catomon.kagamin.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.defaultMediaFolder
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.folder
import org.jetbrains.compose.resources.painterResource
import java.awt.Desktop
import java.io.File
import kotlin.uuid.Uuid

@Preview
@Composable
fun MediaFolderPreview() {
    KagaminTheme {
        Surface {
            val folder = remember {
                defaultMediaFolder.also { it.mkdirs() }
            }

            var currentFolder by remember { mutableStateOf(folder) }

            Column(
                Modifier
                    .fillMaxSize()
                    .background(color = KagaminTheme.colors.backgroundTransparent)
                    .padding(start = 4.dp)
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.height(32.dp).fillMaxWidth()
                ) {
                    Text(
                        currentFolder.nameWithoutExtension,
                        color = KagaminTheme.colors.buttonIcon,
                        modifier = Modifier,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (currentFolder != folder)
                    Text(
                        "< back", Modifier.clickable {
                            currentFolder = currentFolder.parentFile ?: return@clickable
                        }, color = KagaminTheme.colors.buttonIcon,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                Folder(
                    folder = currentFolder,
                    goToFolder = {
                        currentFolder = it
                    },
                    openFile = { file ->
//                        viewModel.play(AudioTrack(uri = file.absolutePath, title = file.nameWithoutExtension, artist = "", album = ""))
                    }
                )
            }
        }
    }
}

@Composable
fun MediaFolder(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    val folder = remember(viewModel.settings) {
        File(viewModel.settings.mediaFolderPath).let {
            if (it.exists()) it else defaultMediaFolder.also { it.mkdirs() }
        }
    }

    var currentFolder by remember { mutableStateOf(folder) }

    Column(
        modifier
            .fillMaxSize()
            .background(color = KagaminTheme.colors.backgroundTransparent)
            .padding(start = 4.dp)
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
                color = KagaminTheme.colors.buttonIcon,
                modifier = Modifier,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (currentFolder != folder)
            Text(
                "< back", Modifier.clickable {
                    currentFolder = currentFolder.parentFile ?: return@clickable
                }.fillMaxWidth(), color = KagaminTheme.colors.buttonIcon,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

        Folder(
            folder = currentFolder,
            goToFolder = {
                currentFolder = it
            },
            openFile = { file ->
                viewModel.play(AudioTrack(uri = file.absolutePath, title = file.nameWithoutExtension, artist = "", album = ""))
            }
        )
    }
}

private val musicExtensions = listOf(
    "mp3", "wav", "flac", "aac", "ogg", "m4a", "wma", "alac", "aiff", "opus"
)

@Composable
fun Folder(
    folder: File,
    goToFolder: (File) -> Unit,
    openFile: (File) -> Unit,
    modifier: Modifier = Modifier
) {
    val files: List<File> = remember(folder) {
        (folder.listFiles()?.toList()?.sortedBy { if (it.isFile) 1 else 0 }
            ?: throw IllegalArgumentException("Is not a folder")).filter { it.extension in musicExtensions || it.isDirectory }
    }

    LazyColumn(modifier.fillMaxWidth()) {
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
        if (file.isDirectory)
            Icon(
                painterResource(Res.drawable.folder),
                null,
                tint = KagaminTheme.colors.buttonIcon,
                modifier = Modifier.size(20.dp)
            )

        Text(
            file.nameWithoutExtension,
            fontSize = 10.sp,
            //color = KagaminTheme.colors.buttonIcon,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 15.sp,
            modifier = Modifier.padding(vertical = 3.dp)
        )
    }
}