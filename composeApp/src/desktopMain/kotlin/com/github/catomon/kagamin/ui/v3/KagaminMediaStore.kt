package com.github.catomon.kagamin.ui.v3

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.catomon.kagamin.data.userDataFolder
import com.github.catomon.kagamin.ui.components.ThumbnailSet
import org.jetbrains.skia.Image as SkiaImage
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.def_thumb_kagami
import kagamin.composeapp.generated.resources.def_thumb_kagami_150
import kagamin.composeapp.generated.resources.def_thumb_kagami_64
import kagamin.composeapp.generated.resources.def_thumb_konata
import kagamin.composeapp.generated.resources.def_thumb_konata_150
import kagamin.composeapp.generated.resources.def_thumb_konata_64
import kagamin.composeapp.generated.resources.def_thumb_lucky_dark
import kagamin.composeapp.generated.resources.def_thumb_lucky_dark_150
import kagamin.composeapp.generated.resources.def_thumb_lucky_dark_64
import kagamin.composeapp.generated.resources.def_thumb_miyuki
import kagamin.composeapp.generated.resources.def_thumb_miyuki_150
import kagamin.composeapp.generated.resources.def_thumb_miyuki_64
import kagamin.composeapp.generated.resources.def_thumb_tsukaremi
import kagamin.composeapp.generated.resources.def_thumb_tsukaremi_150
import kagamin.composeapp.generated.resources.def_thumb_tsukaremi_64

private val imageExtensions = setOf("png", "jpg", "jpeg", "webp", "bmp", "gif")

fun pickAndStoreImage(parent: Frame?, folderName: String, prefix: String): String? {
    val dialog = FileDialog(parent, "Choose image", FileDialog.LOAD)
    dialog.setFilenameFilter { _, name ->
        name.substringAfterLast('.', "").lowercase() in imageExtensions
    }
    dialog.isVisible = true

    val directory = dialog.directory ?: return null
    val name = dialog.file ?: return null
    val source = File(directory, name)
    if (!source.isFile) return null
    if (source.extension.lowercase() !in imageExtensions) return null

    val folder = File(userDataFolder, folderName).also { it.mkdirs() }
    val target = File(
        folder,
        "${prefix}_${System.currentTimeMillis()}.${source.extension.lowercase()}",
    )
    source.copyTo(target, overwrite = true)
    folder.listFiles()
        ?.filter { it != target && it.name.startsWith("${prefix}_") }
        ?.forEach { it.delete() }
    return target.name
}

fun loadStoredPainter(folderName: String, fileName: String): Painter? {
    if (fileName.isBlank()) return null
    val file = File(File(userDataFolder, folderName), fileName)
    if (!file.isFile) return null
    return runCatching {
        BitmapPainter(SkiaImage.makeFromEncoded(file.readBytes()).toComposeImageBitmap())
    }.getOrNull()
}

fun loadStoredBitmap(folderName: String, fileName: String): ImageBitmap? {
    if (fileName.isBlank()) return null
    val file = File(File(userDataFolder, folderName), fileName)
    if (!file.isFile) return null
    return runCatching {
        SkiaImage.makeFromEncoded(file.readBytes()).toComposeImageBitmap()
    }.getOrNull()
}

fun v3ThumbnailSet(themeName: String): ThumbnailSet = when (themeName) {
    "kagami" -> ThumbnailSet(
        Res.drawable.def_thumb_kagami,
        Res.drawable.def_thumb_kagami_64,
        Res.drawable.def_thumb_kagami_150,
    )

    "miyuki" -> ThumbnailSet(
        Res.drawable.def_thumb_miyuki,
        Res.drawable.def_thumb_miyuki_64,
        Res.drawable.def_thumb_miyuki_150,
    )

    "konata" -> ThumbnailSet(
        Res.drawable.def_thumb_konata,
        Res.drawable.def_thumb_konata_64,
        Res.drawable.def_thumb_konata_150,
    )

    "lucky-star-dark" -> ThumbnailSet(
        Res.drawable.def_thumb_lucky_dark,
        Res.drawable.def_thumb_lucky_dark_64,
        Res.drawable.def_thumb_lucky_dark_150,
    )

    else -> ThumbnailSet(
        Res.drawable.def_thumb_tsukaremi,
        Res.drawable.def_thumb_tsukaremi_64,
        Res.drawable.def_thumb_tsukaremi_150,
    )
}
