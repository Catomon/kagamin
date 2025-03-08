package chu.monscout.kagamin.util

private const val INFO = "[I]"
private const val WARN = "[W]"

fun echoMsg(msg: String) {
    println("$INFO $msg")
}

fun echoWarn(msg: String) {
    println("$WARN $msg")
}

fun Any.logMsg(msg: String) {
    println(INFO + " [${this::class.simpleName}]$msg")
}

fun Any.logWarn(msg: String) {
    println(WARN + " [${this::class.simpleName}]$msg")
}