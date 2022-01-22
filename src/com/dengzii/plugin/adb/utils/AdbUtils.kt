package com.dengzii.plugin.adb.utils

import java.io.File
import java.util.concurrent.Executors

/**
 * Utils about ADB.
 *
 * @author github.com/dengzii
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
object AdbUtils {

    private const val SCREEN_CAP_PATH = "/sdcard/"
    private const val SCREEN_RECORD_PATH = "/sdcard/"
    private const val SCREEN_RECORD_TIME_SECOND = 180L
    private const val SCREEN_RECORD_BIT_RATE = 4000000L

    private var adb = "adb"

    fun setAdbCommand(adb: String?) {
        this.adb = adb ?: "adb"
    }

    fun lookingForAdb(): Boolean {
        if (isAdbAvailable()) {
            return true
        }
        val username = System.getenv("USERNAME")
        val os = System.getProperty("os.name").toLowerCase()
        val sdkPath = when {
            os.contains("windows") -> "C:\\Users\\$username\\AppData\\Local\\Android\\Sdk"
            os.contains("linux") -> "/home/$username/Android/Sdk"
            os.contains("mac") -> "/Users/$username/Library/Android/sdk"
            else -> {
                return false
            }
        }
        val adbPath = sdkPath + "${File.separator}platform-tools${File.separator}adb.exe"

        if (checkAdbPath(adbPath)) {
            setAdbCommand(adbPath)
            return true
        }
        return false
    }

    private fun checkAdbPath(path: String): Boolean {
        val a = adb
        setAdbCommand(path)
        val r = isAdbAvailable()
        setAdbCommand(a)
        return r
    }

    fun isAdbAvailable() = version().execute().output.contains("Android Debug Bridge")

    fun version() = getCommand("version")

    /**
     * List all connected devices.
     * @param detail Whether show detail device info.
     */
    fun listDevices(detail: Boolean = true) = getCommand("devices ${if (detail) "-l" else ""}", targeted = false)

    fun startServer() = getCommand("start-server", targeted = false)

    fun killServer() = getCommand("kill-server", targeted = false)

    /**
     * Restart adbd listening on TCP on port.
     * @param port The port.
     */
    fun tcpIp(port: Int, serial: String? = null) = getCommand("tcpip $port", serial)

    /**
     * Restart adbd listening on USB.
     */
    fun usb(serial: String? = null) = getCommand("usb", serial)

    /**
     * Push a single package to the device and install it.
     * @param apkPath The local apk path.
     */
    fun installApk(apkPath: String, serial: String? = null) = getCommand("install $apkPath", serial)

    /**
     * Copy files/dirs from device to local.
     * @param remotePath The remove path.
     * @param compression Whether enable compression.
     * @param preserveTimestampAndMode Whether preserve file timestamp and mode.
     */
    fun pull(
        remotePath: String,
        localPath: String,
        compression: String? = null,
        preserveTimestampAndMode: Boolean = true,
        serial: String? = null
    ) = getCommand("pull" +
            (compression?.let { " -z $compression" } ?: " -Z") +
            " -a".takeOrEmpty(preserveTimestampAndMode) +
            " $remotePath" +
            " $localPath", serial)

    /**
     * Copy local files/dirs to device.
     * @param remotePath The remove path.
     * @param compression Whether enable compression.
     * @param sync If true, only push files that are newer on the host than the device
     */
    fun push(
        remotePath: String,
        localPath: String,
        sync: Boolean = false,
        compression: String? = null,
        serial: String? = null
    ) = getCommand("push" +
            " --sync".takeOrEmpty(sync) +
            (compression?.let { " -z $compression" } ?: " -Z") +
            " $localPath" +
            " $remotePath", serial)

    /**
     * Disconnect from given TCP/IP device, if not specified disconnect all.
     *
     * @param ip The device's ip, if specified means that is TCP/IP device.
     * @param port The device's port, default port is 5555.
     */
    fun disconnect(ip: String? = null, port: Int? = 5555): ADBCommand {
        val address = ip?.let {
            it + port?.let { p -> ":$p" }.orEmpty()
        }.orEmpty()
        return getCommand("disconnect $address", targeted = false)
    }

    /**
     * Connect to a device via TCP/IP.
     *
     * @param ip The device ip.
     * @param port The device port, default is 5555.
     */
    fun connect(ip: String, port: Int? = 5555) =
        getCommand("connect $ip${port?.let { ":$it" }.orEmpty()}", targeted = false)

    /**
     * Start recording screen.
     */
    fun screenRecord(
        path: String = SCREEN_RECORD_PATH,
        timeLimitSecond: Long = SCREEN_RECORD_TIME_SECOND,
        bitRate: Long = SCREEN_RECORD_BIT_RATE,
        serial: String? = null
    ) =
        adbShell(
            "screenrecord " +
                    " --time-limit $timeLimitSecond" +
                    " --bit-rate $bitRate" +
                    " --verbose $path", serial = serial
        )

    fun screenCapture(path: String = SCREEN_CAP_PATH, serial: String? = null) =
        adbShell("screencp $path", serial = serial)

    fun restartServer(): CmdUtils.CmdResult {
        var r = getCommand("kill-server", targeted = false).execute()
        if (r.success) {
            r = getCommand("devices", targeted = false).execute()
        }
        return r
    }

    /**
     * Run shell command on remote device.
     * @param cmd The shell command.
     * @param escape The escape character, or "none"; default '~'
     * @param useStdin Whether read from stdin.
     */
    fun adbShell(
        cmd: String,
        escape: String = "~",
        useStdin: Boolean = true,
        disablePtyAllocation: Boolean = false,
        forcePtyAllocation: Boolean = false,
        disableExitCode: Boolean = false,
        serial: String? = null
    ) = getCommand("shell ${"-n".takeOrEmpty(!useStdin)} $cmd", serial)

    fun getCommand(cmd: String, serial: String? = null, targeted: Boolean = true): ADBCommand {
        val pSerial = if (targeted) {
            serial?.let { " -s $it" }.orEmpty()
        } else {
            ""
        }
        return ADBCommand("$adb$pSerial $cmd")
    }

    private fun String.takeOrEmpty(predicate: Boolean) = if (predicate) this else ""

    private fun String.joinIfNonNullElseEmpty(other: String?) = if (other != null) "$this$other" else ""

    /**
     * Represents an adb command.
     */
    class ADBCommand(val cmd: String) {

        companion object {
            private val EXECUTORS by lazy { Executors.newFixedThreadPool(8) }
        }

        fun execute(): CmdUtils.CmdResult {
            return CmdUtils.execSync(cmd)
        }

        fun execute(callback: (CmdUtils.CmdResult) -> Unit) {
            EXECUTORS.submit {
                callback.invoke(execute())
            }
        }

        override fun toString(): String {
            return "ADBCommand(cmd='$cmd')"
        }
    }
}