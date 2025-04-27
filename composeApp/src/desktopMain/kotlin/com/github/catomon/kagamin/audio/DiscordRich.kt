package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.appName
import com.github.catomon.kagamin.appNameEng
import com.github.catomon.kagamin.loadSettings
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.denpa
import kagamin.composeapp.generated.resources.higurashi
import kagamin.composeapp.generated.resources.nanahira
import kagamin.composeapp.generated.resources.toromi
import net.arikia.dev.drpc.DiscordEventHandlers
import net.arikia.dev.drpc.DiscordRPC
import net.arikia.dev.drpc.DiscordRichPresence
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

var discordRichDisabled = !loadSettings().discordIntegration
var showSongDetails = true

fun startDiscordRich() {
    try {
        DiscordRPC.discordInitialize("1335867032759042058", DiscordEventHandlers(), true)
    } catch (e: Exception) {
        e.printStackTrace()
        discordRichDisabled = true
    }

}

fun stopDiscordRich() {
    try {
        DiscordRPC.discordShutdown()
    } catch (e: Exception) {
        e.printStackTrace()
        discordRichDisabled = true
    }
}

enum class Rich {
    LISTENING,
    PAUSED,
    IDLE,
}

@OptIn(ExperimentalResourceApi::class)
val defSingerDrawableRes = Res.drawable.denpa

@OptIn(ExperimentalResourceApi::class)
class Singer(
    val names: Array<String>,
    var iconIds: Array<String> = arrayOf(
        names.first().lowercase()
    ),
    var drawableRes: DrawableResource = defSingerDrawableRes
) {
    constructor(vararg names: String) : this(arrayOf(*names))

    fun icons(vararg icons: String): Singer {
        iconIds = arrayOf(*icons)
        return this
    }

    @OptIn(ExperimentalResourceApi::class)
    fun res(drawableRes: DrawableResource): Singer {
        this.drawableRes = drawableRes
        return this
    }
}

@OptIn(ExperimentalResourceApi::class)
val singers: Array<Singer> = arrayOf(
    Singer(arrayOf("Nanahira", "ななひら")).res(Res.drawable.nanahira),
    Singer(arrayOf("Toromi", "とろ美")).res(Res.drawable.toromi),
    Singer("ひぐらしのなく頃に", "Higurashi").icons(
        "higurashi",
        "higurashi_satoko",
        "higurashi_rena"
    )
        .res(Res.drawable.higurashi),
    Singer("33.turbo"),
    Singer("Choko"),
    Singer("doubleeleven UpperCut", "doubleeleven undercurrent"),
    Singer("KoronePochi", "ころねぽち", "PochiKorone"),
    Singer("Haruko Momoi"),
    Singer("Innocent Key"),
    Singer("IOSYS"),
    Singer("Koko", "ココ"),
    Singer("Momobako", "桃箱"),
    Singer("MOSAIC.WAV"),
    Singer("nayuta"),
    Singer("Installing!!!").icons("yuu"),
    Singer("東方", "Touhou").icons("touhou", "touhou_2", "touhou_3"),
    Singer("Mili"),
    Singer("The Living Tombstone"),
    Singer("GLAD VALAKAS", "BLEAK FUFEL", "Glad Valakas", "Bleak Fufel", "Глад Валакас"),
    Singer("Blue Stahli")
)

private const val defaultSingerName: String = appNameEng

@OptIn(ExperimentalResourceApi::class)
val denpaSinger = Singer(defaultSingerName).icons("denpa").res(Res.drawable.denpa)

private val AudioTrack.trackInfoString: String get() = info.author + " - " + info.title + " - " + info.uri

val AudioTrack.trackName: String
    get() = if (info.uri.contains("https://")) info.title
    else identifier
        .substring(identifier.lastIndexOf('\\') + 1)
        .substring(identifier.lastIndexOf('/') + 1)
        .removeSuffix(".mp3")

val com.github.catomon.kagamin.audio.AudioTrack.songAuthorPlusTitle get() = "$author - $name"

fun registeredSingerBySongName(track: com.github.catomon.kagamin.audio.AudioTrack): Singer =
    registeredSingerBySongName(track.songAuthorPlusTitle)

fun registeredSingerBySongName(songName: String = defaultSingerName): Singer {
    if (songName == defaultSingerName || songName == "") return denpaSinger

    singers.forEach { singer ->
        if (singer.names.any { it.containedIn(songName) != "" }) {
            return singer
        }
    }

    return denpaSinger
}

val hideSingerIcon = !loadSettings().showSingerIcons

fun discordRich(rich: Rich, track: AudioTrack?) {
    if (discordRichDisabled) return

    val songName = track?.trackName

    val singer = registeredSingerBySongName(track?.trackInfoString ?: defaultSingerName)
    val singerName = singer.names.first()
    val singerIconId = singer.iconIds.random().replace(".", "_").replace(" ", "_").lowercase()

    val presence = DiscordRichPresence().apply {
        when (rich) {
            Rich.IDLE -> {
                largeImageKey = "kagamin512"
                largeImageText = appName
                smallImageKey = "idle"
                smallImageText = "待機中"
                details = "待機中" //"Idle"
            }

            Rich.LISTENING -> {
                largeImageKey = if (hideSingerIcon) "kagamin512" else singerIconId
                largeImageText = if (hideSingerIcon) appName else singerName
                smallImageKey = "playing"
                smallImageText = "視聴中" // "Listening"

                if (showSongDetails) {
                    details = "$songName"
                    endTimestamp = System.currentTimeMillis() + (track?.duration
                        ?: Long.MAX_VALUE) - (track?.position ?: 0L)
                }
            }

            Rich.PAUSED -> {
                largeImageKey = if (hideSingerIcon) "kagamin512" else singerIconId
                largeImageText = if (hideSingerIcon) appName else singerName
                smallImageKey = "paused"
                smallImageText = "一時停止中" // "Paused"
                if (showSongDetails)
                    details = "$songName"
            }
        }
    }

    DiscordRPC.discordUpdatePresence(presence)
}

/** returns empty string if not contained, this otherwise */
private fun String.containedIn(string: String) =
    if (string.lowercase().contains(this.lowercase())) this else ""
