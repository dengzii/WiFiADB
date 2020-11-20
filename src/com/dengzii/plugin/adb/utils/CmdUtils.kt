package com.dengzii.plugin.adb.utils

import com.dengzii.plugin.adb.XLog
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.Executors

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/10/4
 * desc   : Utils about system command executes.
 * </pre>
 */
object CmdUtils {

    private val CMD_EXECUTOR by lazy { Executors.newFixedThreadPool(4) }

    /**
     * Execute the system command in other thread.
     *
     * @param cmd The system command.
     * @param callback The result listener.
     */
    fun exec(cmd: String, callback: ((CmdResult) -> Unit)?) {
        XLog.d(cmd)
        try {
            val process = Runtime.getRuntime().exec(cmd)
            CMD_EXECUTOR.submit {
                callback?.invoke(resolve(process))
            }
        } catch (e: IOException) {
            XLog.e(e)
            callback?.invoke(CmdResult.of(e))
        }
    }

    /**
     * Execute the system command in the current thread.
     *
     * @param cmd The system command.
     * @return The result.
     */
    fun execSync(cmd: String): CmdResult {
        XLog.d(cmd)
        return try {
            val process = Runtime.getRuntime().exec(cmd)
            val error = resolveError(process)?.apply {
                process.destroy()
            }
            error ?: resolve(process)
        } catch (e: IOException) {
            XLog.e(e)
            CmdResult.of(e)
        }
    }

    /**
     * Handle result from the process InputStream.
     *
     * @param process The command process need to resolve.
     * @return The error result, null if no error.
     */
    private fun resolve(process: Process): CmdResult {

        val result = CmdResult()
        val input = process.inputStream

        try {
            val bf = BufferedReader(InputStreamReader(input))
            val builder = StringBuilder()
            bf.lines().forEach {
                XLog.d(">$it")
                builder.append("$it\n")
            }
            result.success = true
            result.output = builder.toString()
        } catch (e: IOException) {
            XLog.e(e)
            result.output = e.message ?: e.localizedMessage
        } finally {
            try {
                input.close()
            } catch (e: IOException) {
                XLog.e(e)
                result.output = e.message ?: e.localizedMessage
            }
        }
        result.exitCode = process.exitValue()
        process.destroy()
        return result
    }

    /**
     * Handle error result from the process InputStream.
     *
     * @param process The command process need to resolve.
     * @return The error result, null if no error.
     */
    private fun resolveError(process: Process): CmdResult? {

        val inputStream = process.inputStream
        var error = false
        try {
            val reader = InputStreamReader(inputStream)
            val bf = BufferedReader(reader)
            val builder = StringBuilder()
            bf.lines().forEach {
                XLog.e(it)
                builder.append("$it\n")
            }
            // if error string is blank means there is no error.
            if (builder.toString().isBlank()) return null
            error = true
            return CmdResult(process.exitValue(), builder.toString())
        } catch (e: IOException) {
            XLog.e(e)
        } finally {
            try {
                if (error) inputStream.close()
            } catch (e: IOException) {
                XLog.e(e)
            }
        }
        return null
    }

    /**
     * Represents the result of a command execution.
     */
    class CmdResult(
            var exitCode: Int = 0,
            var output: String = "",
            var success: Boolean = exitCode == 0,
    ) {
        companion object {
            fun of(e: Exception) = CmdResult(-1, e.message ?: "No message.", false)
        }
    }
}
