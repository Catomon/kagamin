package com.github.catomon.kagamin

import com.github.catomon.kagamin.data.loadSettings

const val appNameJp = "かがみん"
const val appNameEng = "Kagamin"

var appName = if (loadSettings().japanese) appNameJp else appNameEng