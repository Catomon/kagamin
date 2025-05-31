package com.github.catomon.kagamin.util

fun <A> A.alsoPrintIt() = echoMsg { this.toString() }.let { this }