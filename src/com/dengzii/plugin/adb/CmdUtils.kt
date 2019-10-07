package com.dengzii.plugin.adb

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/10/4
 * desc   :
</pre> *
 */
object CmdUtils {

    @JvmStatic
    fun main(args: Array<String>) {
        exec("adb devices", object : CmdListener {
            override fun onExecuted(success: Boolean, code: Int, msg: String) {
                println("==$msg==")
            }
        })
    }

    fun exec(cmd: String, listener: CmdListener) {

        try {
            val process = Runtime.getRuntime().exec(cmd)
            resolve(process.inputStream, listener)
//            resolve(process.errorStream, listener)
        } catch (e: IOException) {
            e.printStackTrace()
            e.message?.let { listener.onExecuted(false, -1, it) }
        }

    }

    private fun resolve(input: InputStream, listener: CmdListener) = Thread {
        val reader = InputStreamReader(input)
        val bf = BufferedReader(reader)
        try {
            val builder = StringBuilder()
            bf.lines().forEach {
                builder.append("$it\n")
            }
            listener.onExecuted(true, 0, builder.toString())
        } catch (e: IOException) {
            e.printStackTrace()
            e.message?.let { listener.onExecuted(false, -1, it) }
        } finally {
            try {
                input.close()
            } catch (e: IOException) {
                e.message?.let { listener.onExecuted(false, -1, it) }
            }

        }

    }.start()

    interface CmdListener {
        fun onExecuted(success: Boolean, code: Int, msg: String)
    }
}
