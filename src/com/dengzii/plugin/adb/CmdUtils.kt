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
 * </pre>
 */
object CmdUtils {

    private const val TAG = "CmdUtils"

    fun adbShell(sh: String, listener: CmdListener?) {
        exec("adb shell $sh", listener)
    }

    fun adbShellSync(sh: String): CmdResult {
        return execSync("adb shell $sh")
    }

    fun adbShell(device: Device, sh: String, listener: CmdListener?) {
        exec("adb -s ${device.sn} shell $sh", listener)
    }

    fun exec(cmd: String, listener: CmdListener? = null) {

        XLog.d("$TAG.exec", cmd)
        try {
            val process = Runtime.getRuntime().exec(cmd)
            Thread(Runnable {
                val result = resolve(process)
                listener?.onExecuted(result.success, result.exitCode, result.info)
            }).start()
        } catch (e: IOException) {
            e.message?.let { listener?.onExecuted(false, -1, it) }
        }
    }

    fun execSync(cmd: String): CmdResult {
        XLog.d("$TAG.execSync", cmd)
        return try {
            val process = Runtime.getRuntime().exec(cmd)
            resolve(process)
        } catch (e: IOException) {
            XLog.e("$TAG.execSync", e)
            CmdResult()
        }
    }

    private fun resolve(process: Process): CmdResult {

        val result = CmdResult()
        val input = process.inputStream
        resolveErr(process.errorStream)
        val reader = InputStreamReader(input)
        val bf = BufferedReader(reader)
        try {
            val builder = StringBuilder()
            bf.lines().forEach {
                XLog.d("$TAG.resolve", it)
                builder.append("$it\n")
            }
            result.success = true
            result.info = builder.toString()
        } catch (e: IOException) {
            XLog.e("$TAG.resolve", e)
            result.info = e.message ?: e.localizedMessage
        } finally {
            try {
                input.close()
            } catch (e: IOException) {
                XLog.e("$TAG.resolveSync", e)
                result.info = e.message ?: e.localizedMessage
            }
        }
        result.exitCode = process.exitValue()
        process.destroy()
        return result
    }

    private fun resolveErr(inputStream: InputStream) {
        val reader = InputStreamReader(inputStream)
        val bf = BufferedReader(reader)
        try {
            val builder = StringBuilder()
            bf.lines().forEach {
                XLog.e("$TAG.resolveErr", it)
                builder.append("$it\n")
            }
            builder.toString()
        } catch (e: IOException) {
            XLog.e("$TAG.resolveErr", e)
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                XLog.e("$TAG.resolveErr", e)
            }
        }
    }
}
