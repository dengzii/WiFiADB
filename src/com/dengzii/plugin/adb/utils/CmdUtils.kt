package com.dengzii.plugin.adb.utils

import com.dengzii.plugin.adb.XLog
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.Executors

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/10/4
 * desc   :
 * </pre>
 */
object CmdUtils {

    private const val TAG = "CmdUtils"
    private val CMD_EXECUTOR = Executors.newSingleThreadExecutor()

//    fun scanDevice() {
//
//        try {
//            val socket = Socket("192.168.0.108", 5555)
//            println("Connected to " + socket.inetAddress + " on port " +
//                    socket.port + " from port " + socket.localPort + " of " + socket.localAddress);
//        }catch (e:Throwable){
//            println("not open.")
//        }
//
//    }


    /**
     * Execute system command async.
     *
     * @param cmd The system command.
     * @param listener The result listener.
     */
    fun exec(cmd: String, listener: CmdListener? = null) {

        XLog.d("$TAG.exec", cmd)
        try {
            val process = Runtime.getRuntime().exec(cmd)
            CMD_EXECUTOR.submit {
                val result = resolve(process)
                listener?.onExecuted(result.success, result.exitCode, result.output)
            }
        } catch (e: IOException) {
            e.message?.let { listener?.onExecuted(false, -1, it) }
        }
    }

    /**
     * Execute system command sync.
     *
     * @param cmd The system command.
     * @return The result.
     */
    fun execSync(cmd: String): CmdResult {
        XLog.d("$TAG.execSync", cmd)
        return try {
            val process = Runtime.getRuntime().exec(cmd)
            val error = resolveErr(process.errorStream)
            error ?: resolve(process)
        } catch (e: IOException) {
            XLog.e("$TAG.execSync", e)
            CmdResult()
        }
    }

    private fun resolve(process: Process): CmdResult {

        val result = CmdResult()
        val input = process.inputStream

        val reader = InputStreamReader(input)
        val bf = BufferedReader(reader)
        try {
            val builder = StringBuilder()
            bf.lines().forEach {
                XLog.d("$TAG.resolve", it)
                builder.append("$it\n")
            }
            result.success = true
            result.output = builder.toString()
        } catch (e: IOException) {
            XLog.e("$TAG.resolve", e)
            result.output = e.message ?: e.localizedMessage
        } finally {
            try {
                input.close()
            } catch (e: IOException) {
                XLog.e("$TAG.resolveSync", e)
                result.output = e.message ?: e.localizedMessage
            }
        }
        result.exitCode = process.exitValue()
        process.destroy()
        return result
    }

    private fun resolveErr(inputStream: InputStream): CmdResult? {
        val reader = InputStreamReader(inputStream)
        val bf = BufferedReader(reader)
        try {
            val builder = StringBuilder()
            bf.lines().forEach {
                XLog.e("$TAG.resolveErr", it)
                builder.append("$it\n")
            }
            if (builder.toString().isBlank()) return null
            return CmdResult(-1, builder.toString(), false)
        } catch (e: IOException) {
            XLog.e("$TAG.resolveErr", e)
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                XLog.e("$TAG.resolveErr", e)
            }
        }
        return null
    }
}
