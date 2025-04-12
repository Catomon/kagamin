package chu.monscout.kagamin

import java.io.File

actual val userDataFolder: File =
    File(
        System.getProperty("user.home"),
        if (osName.contains("win")) "AppData/Roaming/Kagamin" else ".local/share/Kagamin"
    )