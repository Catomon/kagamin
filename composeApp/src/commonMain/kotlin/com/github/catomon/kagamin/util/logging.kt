package com.github.catomon.kagamin.util

import com.github.catomon.kagamin.util.Rogga.log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private const val INFO = "[I]"
private const val WARN = "[W]"

@Deprecated("lame", replaceWith = ReplaceWith("echoMsg { msg }"))
fun echoMsg(msg: String) {
    println("$INFO $msg")
}

@Deprecated("lame", replaceWith = ReplaceWith("echoWarn { msg }"))
fun echoWarn(msg: String) {
    System.err.println("$WARN $msg")
}

@Deprecated("lame", replaceWith = ReplaceWith("logMsg { msg }"))
fun Any.logMsg(msg: String) {
    println(INFO + "[${this::class.simpleName}] $msg")
}

@Deprecated("lame", replaceWith = ReplaceWith("logWarn { msg }"))
fun Any.logWarn(msg: String) {
    System.err.println(WARN + "[${this::class.simpleName}] $msg")
}

enum class LogLevel(val priority: Int) {
    TRACE(-1),
    DEBUG(0),
    INFO(1),
    WARN(2),
    ERROR(3),
    FATAL(4)
}

object Rogga {
    var logLevel = LogLevel.INFO
    var timestamp: Boolean = false

    val fullFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    val timeOnlyFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    var formatter = timeOnlyFormatter

    inline fun log(
        level: LogLevel,
        tag: String = "",
        crossinline msg: () -> String
    ) {
        if (level.priority >= Rogga.logLevel.priority) {
            val message: String
            if (Rogga.timestamp) {
                val timestamp = LocalDateTime.now().format(Rogga.formatter)
                val thread = Thread.currentThread().name
                message = "$timestamp $thread ${level.name} $tag${msg()}"
            } else {
                message = "${level.name} $tag${msg()}"
            }
            if (level >= LogLevel.WARN) {
                System.err.println(message)
            } else {
                println(message)
            }
        }
    }
}

inline fun echoFatal(crossinline msg: () -> String) {
    log(LogLevel.FATAL, "", msg)
}

inline fun Any.logFatal(crossinline msg: () -> String) {
    log(LogLevel.FATAL, "[${this::class.simpleName}] ", msg)
}

inline fun echoTrace(crossinline msg: () -> String) {
    log(LogLevel.TRACE, "", msg)
}

inline fun Any.logTrace(crossinline msg: () -> String) {
    log(LogLevel.TRACE, "[${this::class.simpleName}] ", msg)
}

inline fun echoDbg(crossinline msg: () -> String) {
    log(LogLevel.DEBUG, "", msg)
}

inline fun echoMsg(crossinline msg: () -> String) {
    log(LogLevel.INFO, "", msg)
}

inline fun echoWarn(crossinline msg: () -> String) {
    log(LogLevel.WARN, "", msg)
}

inline fun echoMsg(format: String, vararg args: Any?) {
    log(LogLevel.INFO) { format.format(*args) }
}

inline fun Any.logMsg(format: String, vararg args: Any?) {
    log(LogLevel.INFO, "[${this::class.simpleName}] ") { format.format(*args) }
}

inline fun echoErr(crossinline msg: () -> String) {
    log(LogLevel.ERROR, "", msg)
}

inline fun Any.logDbg(crossinline msg: () -> String) {
    log(LogLevel.DEBUG, "[${this::class.simpleName}] ", msg)
}

inline fun Any.logMsg(crossinline msg: () -> String) {
    log(LogLevel.INFO, "[${this::class.simpleName}] ", msg)
}

inline fun Any.logWarn(crossinline msg: () -> String) {
    log(LogLevel.WARN, "[${this::class.simpleName}] ", msg)
}

inline fun Any.logErr(crossinline msg: () -> String) {
    log(LogLevel.ERROR, "[${this::class.simpleName}] ", msg)
}

inline fun echoErr(exception: Throwable, crossinline msg: () -> String) {
    log(LogLevel.ERROR) { "${msg()}\n${exception.stackTraceToString()}" }
}

inline fun Any.logErr(exception: Throwable, crossinline msg: () -> String) {
    log(
        LogLevel.ERROR,
        "[${this::class.simpleName}] "
    ) { "${msg()}\n${exception.stackTraceToString()}" }
}

val lastSentMap = ConcurrentHashMap<String, Long>()
val skippedCountMap = ConcurrentHashMap<String, AtomicInteger>()

inline fun echoTraceFiltered(crossinline msg: () -> String) {
    val message = msg()
    val now = System.currentTimeMillis()

    val lastSent = lastSentMap[message] ?: 0L
    if (now - lastSent >= 1000) {
        lastSentMap[message] = now

        val skipped = skippedCountMap[message]?.getAndSet(0) ?: 0
        if (skipped > 0) {
            log(LogLevel.TRACE, "", { "$message (skipped $skipped similar messages)" })
        } else {
            log(LogLevel.TRACE, "", { message })
        }
    } else {
        skippedCountMap.computeIfAbsent(message) { AtomicInteger(0) }.incrementAndGet()
    }
}