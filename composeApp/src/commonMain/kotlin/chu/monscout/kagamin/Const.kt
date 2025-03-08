package chu.monscout.kagamin

const val appNameJp = "かがみん"
const val appNameEng = "Kagamin"

var appName = if (loadSettings().japaneseTitle) appNameJp else appNameEng