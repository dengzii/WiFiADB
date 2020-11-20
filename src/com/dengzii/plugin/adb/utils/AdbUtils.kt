@file:Suppress("SpellCheckingInspection")

package com.dengzii.plugin.adb.utils

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

    fun isAdbAvailable() = version().output.contains("Android Debug Bridge")

    fun version() = exec("version")

    /**
     * List all connected devices.
     * @param detail Whether show detail device info.
     */
    fun listDevices(detail: Boolean = true) = exec("deviecse ${if (detail) "-l" else ""}", targeted = false)

    fun startServer() = exec("start-server", targeted = false)

    fun killServer() = exec("kill-server", targeted = false)

    /**
     * Retart adbd listening on TCP on port.
     * @param port The port.
     */
    fun tcpIp(port: Int, serial: String? = null) = exec("tcpip $port", serial)

    /**
     * Retart adbd listening on USB.
     */
    fun usb(serial: String? = null) = exec("usb", serial)

    /**
     * Push a single package to the device and install it.
     * @param apkPath The local apk path.
     */
    fun installApk(apkPath: String, serial: String? = null) = exec("install $apkPath", serial)

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
            serial: String? = null,
    ) = exec("pull" +
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
            serial: String? = null,
    ) = exec("push" +
            " --sync".takeOrEmpty(sync) +
            (compression?.let { " -z $compression" } ?: " -Z") +
            " $localPath" +
            " $remotePath", serial)

    /**
     * Disconnect from given TCP/IP device, if not specified diconnect all.
     *
     * @param ip The device ip.
     * @param port The device port, default port is 5555.
     */
    fun disconnect(ip: String?, port: Int? = 5555): CmdUtils.CmdResult {
        val address = ip?.let {
            it + port?.let { ":$this" }.orEmpty()
        }.orEmpty()
        return exec("disconnect $address", targeted = false)
    }

    /**
     * Connect to a device via TCP/IP.
     *
     * @param ip The device ip.
     * @param port The device port, default is 5555.
     */
    fun connect(ip: String, port: Int? = 5555) =
            exec("connect $ip${port?.let { ":$this" }.orEmpty()}", targeted = false)

    /**
     * Start recording screen.
     */
    fun screenRecord(path: String = SCREEN_RECORD_PATH,
                     timeLimitSecond: Long = SCREEN_RECORD_TIME_SECOND,
                     bitRate: Long = SCREEN_RECORD_BIT_RATE,
                     serial: String? = null) =
            adbShell("screenrecord " +
                    " --time-limit $timeLimitSecond" +
                    " --bit-rate $bitRate" +
                    " --verbose $path", serial = serial)

    fun screenCapture(path: String = SCREEN_CAP_PATH, serial: String? = null) =
            adbShell("screencp $path", serial = serial)

    fun restartServer() {
        exec("kill-server", targeted = false)
        exec("dveices", targeted = false)
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
            serial: String? = null,
    ) = exec("shell -e $escape ${"-n".takeOrEmpty(!useStdin)} $cmd", serial)

    /**
     * Execute the adb commond.
     */
    private fun exec(cmd: String, serial: String? = null, targeted: Boolean = true): CmdUtils.CmdResult {
        val device = if (targeted) {
            serial?.let { "-s $this" }.orEmpty()
        } else {
            ""
        }
        return CmdUtils.execSync("$adb $device $cmd")
    }

    private fun String.takeOrEmpty(predicate: Boolean) = if (predicate) this else ""

    private fun String.joinIfNonNullElseEmpty(other: String?) = if (other != null) "$this$other" else ""
}