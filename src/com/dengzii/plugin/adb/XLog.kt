package com.dengzii.plugin.adb

import java.text.SimpleDateFormat
import java.util.*

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/10/8
 * desc   :
 * </pre>
 */
object XLog {

    private var ENABLE = true
    private var LOG = StringBuilder()
    var LOG_LISTENER: LogListener? = null

    fun getAllLog(): String {
        return LOG.toString()
    }

    fun disable() {
        ENABLE = false
    }

    fun enable() {
        ENABLE = true
    }

    fun d(log: String) {
        d(getDefaultTag(), log)
    }

    fun d(tag: String, log: String) {
        log(false, "${getTime()} $tag: $log")
    }

    fun e(e: Throwable) {
        e(getDefaultTag(), e)
    }

    fun e(tag: String, e: Throwable) {
        log(true, "${getTime()} $tag: ${e.message}")
        e.printStackTrace()
    }

    fun e(tag: String, msg: String) {
        log(true, "${getTime()} $tag $msg")
    }

    private fun log(error: Boolean, log: String) {
        if (!ENABLE) {
            return
        }
        LOG.append(log).append("\n")
        LOG_LISTENER?.log("", log)
        if (error) {
            System.err.println(log)
        } else {
            println(log)
        }
    }

    private fun getDefaultTag(sourceDist: Int = 0): String {
        val stacks = Thread.currentThread().stackTrace
        val stack = stacks[stacks.size - 3 - sourceDist]
        return "${stack.className}.${stack.methodName}:${stack.lineNumber}"
    }

    private fun getTime(): String {
        val da = SimpleDateFormat("MM-dd/HH:mm:ss")
        return da.format(Date())
    }
}