package com.github.catomon.kagamin.ui.util

fun formatMillisToMinutesSeconds(milliseconds: Long?): String {
    return if (milliseconds == null) {
        "-:-"
    } else {
        val minutes = milliseconds / 1000L / 60L
        val seconds = (milliseconds / 1000L % 60)
        String.format("%d:%02d", minutes, seconds)
    }
}