package chu.monscout.kagamin

import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import kagamin.composeapp.generated.resources.BadComic_Regular
import kagamin.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun YukiTheme(content: @Composable () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState,
    ) {
        MaterialTheme(
            colors = Colors.yukiColors,
            typography = Typography(FontFamily(Font(Res.font.BadComic_Regular))),
            content = content
        )
    }
}

object Themes {
    val list = listOf(Violet, Pink, Blue, KagaminDark)

    fun forName(name: String) = list.find { it.name == name }

    fun forNameOrFirst(name: String) = list.find { it.name == name } ?: list.first()

    object Pink : IYukiTheme(
        name = "yuki",
        background = Color(0xffffbecf),
        background2 = Color(0xffff618f),
        surface = Color(0xfff799b4),
        font = Color(0xFFFFFFFF),
        fontSecondary = Color(0xFFFFE1EA),
        bars = Color(0xffec588c),
    ) {
        override val smallButtonIcon: Color = Color(0xffffffff)
        override val playerButtonIcon: Color = Color(0xffffc3d2)
        override val onBars: Color = Color(0xfff58a9e)
        override val listItemA: Color = Color(0xcdff84a6)
        override val listItemB: Color = Color(0xcdff618f)
        override val barsTransparent: Color = bars.copy(0.8f)
    }

    //more pink
    //   object Pink : IYukiTheme(
    //        name = "yuki",
    //        background = Color(0xffffbecf),
    //        background2 = Color(0xffff618f),
    //        surface = Color(0xfff799b4),
    //        font = Color(0xFFFFFFFF),
    //        fontSecondary = Color(0xFFFFE1EA),
    //        bars = Color(0xffec588c),
    //    ) {
    //
    //    }

    //blue + pnk
    //    object Pink : IYukiTheme(
    //        name = "yuki",
    //        background = Color(0xffffbecf),
    //        background2 = Color(0xffc94d63),
    //        surface = Color(0xfff799b4),
    //        font = Color(0xFFFFFFFF),
    //        fontSecondary = Color(0xFFFFE1EA),
    //        bars = Color(0xffe57189),
    //    ) {
    //        override val smallButtonIcon: Color = Color(0xfffdd0d9)
    //        override val playerButtonIcon: Color = Color(0xfffdd0d9)
    //        override val onBars: Color = Color(0xffe5667d)
    //        override val listItemA: Color = Color(0xe585c5e9)
    //        override val listItemB: Color = Color(0xe55eb0dc)
    //        override val barsTransparent: Color = bars.copy(0.8f)
    //    }

    object Violet : IYukiTheme(
        name = "gami-kasa",
        background = Color(0xffc09dff),
        background2 = Color(0xff6232a9),
        surface = Color(0xff916dd6),
        font = Color(0xFFFFFFFF),
        fontSecondary = Color(0xFFDECCFF),
        bars = Color(0xff6232a9),
    ) {
        override val playerButtonIcon: Color = Color(0xff9775d5)
        override val listItemA: Color = Color(0xcb753cc9)
        override val listItemB: Color = Color(0xcb6632b2)
        override val thinBorder: Color = Color(0xff5522a2)
    }

    object Blue : IYukiTheme(
        name = "nata",
        background = Color(0xffadcfff),
        background2 = Color(0xff0f2e93),
        surface = Color(0xff6197de),
        font = Color(0xFFFFFFFF),
        fontSecondary = Color(0xffd2e6ff),
        bars = Color(0xff0f2e93),
    ) {
//        override val playerButtonIcon: Color = Color(0xff9775d5)
        override val listItemA: Color = Color(0xcc1840c5)
        override val listItemB: Color = Color(0xcc1037b4)
    }

    //    object YukiLight : IYukiTheme(
    //        name = "yuki-light",
    //        background = Color(0xffffffff),
    //        background2 = Color(0xff86c6ea),
    //        surface = Color(0xffb3e7fd),
    //        font =  Color(0xff49b5fc), //Color(0xffff4d7c)
    //        fontSecondary = Color(0xff2d9ae1),
    //        bars = Color(0xff0089de),
    //    ) {
    //        override val smallButtonIcon: Color = Color(0xffff4d7c)
    //        override val playerButtonIcon: Color = Color(0xfffd7299)
    //        override val onBars: Color = Color(0xfffd9bb7)
    //        override val listItemA: Color = Color(0x80b3e7fd)
    //        override val listItemB: Color = Color(0x8071c9fc)
    //        override val barsTransparent: Color = bars.copy(0.50f)
    //    }

    object KagaminDark : IYukiTheme(
        name = "kagamin-dark",
        background = Color(0xffffffff),
        background2 = Color(0xff8a8a8a),
        surface = Color(0xffb2b2b2),
        font = Color(0xffffffff), //Color(0xffff4d7c)
        fontSecondary = Color(0xffab0460),
        bars = Color(0xff000000),
    ) {
        override val smallButtonIcon: Color = Color(0xffd51e82)
        override val playerButtonIcon: Color = Color(0xffd51e82)
        override val onBars: Color = Color(0xfffd72bb)
        override val listItemA: Color = Color(0xe6de2d8d)
        override val listItemB: Color = Color(0xe6d51e82)
        override val barsTransparent: Color = bars.copy(0.95f)
    }

    open class IYukiTheme(
        val name: String,
        val background: Color,
        val background2: Color,
        val surface: Color,
        val font: Color,
        val fontSecondary: Color,
        val bars: Color,
    ) {
        open val smallButtonIcon: Color = surface
        open val playerButtonIcon: Color = surface
        open val onBars: Color = surface
        open val listItemA: Color = surface.copy(0.9f)
        open val listItemB: Color = background2.copy(0.8f)
        open val barsTransparent: Color = bars.copy(0.9f)
        open val playerButtonIconTransparent get() = playerButtonIcon.copy(0.5f)
        open val progressOverThumbnail: Color = bars.copy(0.5f)
        open val thinBorder: Color = bars
    }
}

object Colors {
    var currentYukiTheme = Themes.list.first()

    val surface get() = currentYukiTheme.surface
    val text get() = currentYukiTheme.font
    val text2 get() = currentYukiTheme.fontSecondary
    val background get() = currentYukiTheme.background
    val bars get() = currentYukiTheme.bars
    val barsTransparent get() = currentYukiTheme.barsTransparent

    init {
        currentYukiTheme =
            Themes.list.find { it.name == loadSettings().theme } ?: Themes.list.first()
    }

    private val lightRed = Color(245, 83, 95)
    var yukiColors = androidx.compose.material.Colors(
        primary = bars,
        primaryVariant = Color(0xFF3700B3),
        secondary = Color(0xFF03DAC6),
        secondaryVariant = Color(0xFF018786),
        background = background,
        surface = bars,
        error = lightRed,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
        onError = Color.White,
        false
    )
}