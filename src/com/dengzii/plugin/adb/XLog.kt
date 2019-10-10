package com.dengzii.plugin.adb

import java.text.SimpleDateFormat
import java.util.*

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/10/8
 * desc   :
 * </pre>
 */
object XLog {

    private var TURN_ON_LOG = true
    private var LOG = StringBuilder()
    var LOG_LISTENER: LogListener? = null

    fun getLog(): String {
        return LOG.toString()
    }

    fun disable() {
        TURN_ON_LOG = false
    }

    fun enable() {
        TURN_ON_LOG = true
    }

    fun d(tag: String, log: String) {
        log(false, "${getTime()} $tag: $log")
    }

    fun e(tag: String, e: Throwable) {
        log(true, "${getTime()} $tag: ${e.message}")
        e.printStackTrace()
    }

    fun e(tag: String, msg: String) {
        log(true, "${getTime()} $tag $msg")
    }

    private fun log(error: Boolean, log: String) {
        if (!TURN_ON_LOG) {
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

    private fun getTime(): String {
        val da = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        return da.format(Date())
    }
}