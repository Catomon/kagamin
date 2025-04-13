package chu.monscout.kagamin.ui.theme

import androidx.compose.ui.graphics.Color

sealed class KagaminTheme(
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
    open val selectedButton: Color = Color.White

    object White : KagaminTheme(
        name = "white",
        background = Color(0xffc9c9c9),
        background2 = Color(0xff626262),
        surface = Color(0xffd9d9d9),
        font = Color(0xff111111),
        fontSecondary = Color(0xff464646),
        bars = Color(0xffffffff),
    ) {
        override val playerButtonIcon: Color = Color(0xffb6b6b6)
        override val listItemA: Color = Color(0xcbeaeaea)
        override val listItemB: Color = Color(0xcbcccccc)
        override val thinBorder: Color = Color(0xff5b5b5b)
        override val selectedButton: Color = fontSecondary
    }

    object Grey : KagaminTheme(
        name = "grey",
        background = Color(0xffc9c9c9),
        background2 = Color(0xff626262),
        surface = Color(0xffd9d9d9),
        font = Color(0xff111111),
        fontSecondary = Color(0xff464646),
        bars = Color(0xff3d3d3d),
    ) {
        override val playerButtonIcon: Color = Color(0xffb6b6b6)
        override val listItemA: Color = Color(0xcbcecece)
        override val listItemB: Color = Color(0xcbb6b6b6)
        override val thinBorder: Color = Color(0xff5b5b5b)
    }

    object Pink : KagaminTheme(
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
        override val thinBorder: Color = Color(0xffea417c)
        override val selectedButton: Color = background2
    }

    //more pink
    //   object Pink : KagaminTheme(
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
    //    object Pink : KagaminTheme(
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

    object Violet : KagaminTheme(
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

    object Blue : KagaminTheme(
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
        override val thinBorder: Color = Color(0xff042386)
    }

    //    object YukiLight : KagaminTheme(
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

    object KagaminDark : KagaminTheme(
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
        override val progressOverThumbnail: Color = bars.copy(0.25f)
    }
}