package com.dengzii.plugin.adb.utils

object AndroidShellUtils {

    var serial: String = ""

    fun sdkVersion() {
        shell("getprop ro.build.version.sdk")
    }

    fun releaseVersion() {
        shell("getprop ro.build.version.release")
    }

    fun brand() {
        shell("getprop ro.product.brand")
    }

    private fun shell(shell: String) = AdbUtils.adbShell(shell, serial = serial.ifEmpty { null })

    class Sheller<T>(adbCommand: AdbUtils.ADBCommand) {

        fun r() {

        }
    }
}