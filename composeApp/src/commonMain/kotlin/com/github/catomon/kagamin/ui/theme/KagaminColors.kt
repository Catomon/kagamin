package com.github.catomon.kagamin.ui.theme

import androidx.compose.ui.graphics.Color

sealed class KagaminColors(
    val name: String,
    open val behindBackground: Color,
    open val text: Color,
    open val textSecondary: Color,
    open val background: Color,
    open val forDisabledMostlyIdk: Color,
    open val listItem: Color,
    open val buttonIconSmall: Color = forDisabledMostlyIdk,
    open val buttonIcon: Color = forDisabledMostlyIdk,
    open val onBars: Color = forDisabledMostlyIdk,
    open val backgroundTransparent: Color = background.copy(0.9f),
    open val buttonIconTransparent: Color = buttonIcon.copy(0.5f),
    open val thumbnailProgressIndicator: Color = background.copy(0.5f),
    open val thinBorder: Color = background,
    open val buttonIconSmallSelected: Color = Color.White,
) {

    //    open val listItemA: Color = surface.copy(0.9f)
    //    open val listItemB: Color = background2.copy(0.8f)
    //    open val barsTransparent: Color = bars
    //    open val playerButtonIconTransparent get() = playerButtonIcon
    //    open val progressOverThumbnail: Color = bars

    object White : KagaminColors(
        name = "white",
        behindBackground = Color(0xffc9c9c9),
        listItem = Color(0xff626262),
        forDisabledMostlyIdk = Color(0xffd9d9d9),
        text = Color(0xff111111),
        textSecondary = Color(0xff464646),
        background = Color(0xffffffff),
    ) {
        override val buttonIcon: Color = Color(0xffb6b6b6)
        override val forDisabledMostlyIdk: Color = Color(0xcbeaeaea)
        override val listItem: Color = Color(0xcbcccccc)
        override val thinBorder: Color = Color(0xff5b5b5b)
        override val buttonIconSmallSelected: Color = textSecondary
    }

    object Grey : KagaminColors(
        name = "grey",
        behindBackground = Color(0xffc9c9c9),
        listItem = Color(0xff626262),
        forDisabledMostlyIdk = Color(0xffd9d9d9),
        text = Color(0xff111111),
        textSecondary = Color(0xff464646),
        background = Color(0xff3d3d3d),
    ) {
        override val buttonIcon: Color = Color(0xffb6b6b6)
        override val forDisabledMostlyIdk: Color = Color(0xcbcecece)
        override val listItem: Color = Color(0xcbb6b6b6)
        override val thinBorder: Color = Color(0xff5b5b5b)
    }

    object Pink : KagaminColors(
        name = "yuki",
        behindBackground = Color(0xffffbecf),
        listItem = Color(0xcb753cc9),
        forDisabledMostlyIdk = Color(0xfff799b4),
        text = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFFFE1EA),
        background = Color(0xffec588c),
    ) {
        override val buttonIconSmall: Color = Color(0xffffffff)
        override val buttonIcon: Color = Color(0xffffc3d2)
        override val onBars: Color = Color(0xfff58a9e)
        override val forDisabledMostlyIdk: Color = Color(0xcdff84a6)
        override val backgroundTransparent: Color = background.copy(0.8f)
        override val thinBorder: Color = Color(0xffea417c)
        override val buttonIconSmallSelected: Color = listItem
    }

    //more pink
    //   object Pink : KagaminColors(
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
    //    object Pink : KagaminColors(
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

    object Violet : KagaminColors(
        name = "gami-kasa",
        behindBackground = Color(0xffc09dff),
        listItem = Color(0xcb6c4141),
        forDisabledMostlyIdk = Color(0xcb753cc9),
        text = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFDECCFF),
        background = Color(0xff6232a9),
    ) {
        override val buttonIconSmall: Color = Color(0xff916dd6)
        override val buttonIcon: Color = Color(0xff9775d5)
        override val thinBorder: Color = Color(0xff5522a2)
    }

    object VioletKasa : KagaminColors(
        name = "gami-kasa",
        behindBackground = Color(0xffc09dff),
        listItem = Color(0xcba28a06),
        forDisabledMostlyIdk = Color(0xcb753cc9),
        text = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFDECCFF),
        background = Color(0xff6232a9),
    ) {
        override val buttonIconSmall: Color = Color(0xff916dd6)
        override val buttonIcon: Color = Color(0xff9775d5)
        override val thinBorder: Color = Color(0xff5522a2)
    }

    data object Blue : KagaminColors(
        name = "nata",
        behindBackground = Color(0xffadcfff),
        listItem =  Color(0xcc0c3719),
        forDisabledMostlyIdk = Color(0xff6197de),
        text = Color(0xFFFFFFFF),
        textSecondary = Color(0xffd2e6ff),
        background = Color(0xff0f2e93),
    ) {
        //        override val playerButtonIcon: Color = Color(0xff9775d5)
        override val forDisabledMostlyIdk: Color = Color(0xcc1840c5)
        override val thinBorder: Color = Color(0xff042386)
    }

    //    object YukiLight : KagaminColors(
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

    object KagaminDark : KagaminColors(
        name = "kagamin-dark",
        behindBackground = Color(0xffffffff),
        listItem = Color(0xff8a8a8a),
        forDisabledMostlyIdk = Color(0xffb2b2b2),
        text = Color(0xffffffff), //Color(0xffff4d7c)
        textSecondary = Color(0xffffabda),
        background = Color(0xff000000),
    ) {
        override val buttonIconSmall: Color = Color(0xffd51e82)
        override val buttonIcon: Color = Color(0xffd51e82)
        override val onBars: Color = Color(0xfffd72bb)
        override val forDisabledMostlyIdk: Color = Color(0xcdde2d8d)
        override val listItem: Color = Color(0xcbd51e82)
        override val backgroundTransparent: Color = background.copy(0.95f)
        override val thumbnailProgressIndicator: Color = background.copy(0.25f)
    }
}