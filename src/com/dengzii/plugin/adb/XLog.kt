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

    fun d(tag: String, log: String) {
        println("${getTime()} $tag: $log")
    }

    fun e(tag: String, e: Throwable) {
        System.err.println("${getTime()} $tag: ${e.message}")
        e.printStackTrace()
    }

    fun e(tag: String, msg: String) {
        System.err.println("${getTime()} $tag $msg")
    }

    private fun getTime(): String {
        val da = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        return da.format(Date())
    }
}