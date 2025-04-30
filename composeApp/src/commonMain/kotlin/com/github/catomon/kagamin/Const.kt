package com.github.catomon.kagamin

const val appNameJp = "かがみん"
const val appNameEng = "Kagamin"

var appName = if (loadSettings().japanese) appNameJp else appNameEng