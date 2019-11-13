@file:Suppress("SpellCheckingInspection")

package com.dengzii.plugin.adb.utils

import com.dengzii.plugin.adb.Config
import com.dengzii.plugin.adb.Device
import com.dengzii.plugin.adb.XLog
import java.util.regex.Pattern

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/10/4
 * desc   :
</pre> *
 */
object AdbUtils {

    // Using for matches the cases commandline output are not device info.
    private val LINE_NO_DEVICES = arrayOf(
            "* daemon not running. starting it now on port 5037 *",
            "* daemon started successfully *",
            "List of devices attached",
            "adb server is out of date.  killing...")

    private const val SPACE = " "
    private const val NEW_LINE = "\n"

    private const val SCREEN_CAP_PATH = "/sdcard/"
    private const val SCREEN_RECORD_PATH = "/sdcard/"
    private const val SCREEN_RECORD_TIME_SECOND = 180
    private const val SCREEN_RECORD_BIT_RATE = 4000000

    private const val REGEX_IP = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}"
    private val PATTERN_INET_ADDR = Pattern.compile("inet addr:($REGEX_IP) {2}Bcast:($REGEX_IP)")

    private val DEVICES_CONNECTED = ArrayList<String>()
    private val USED_ADB_PORT = mutableListOf<String>()
    private val DEVICES_TEMP = HashMap<String, Device>()

    fun getConnectedDeviceList(): List<Device> {

        // clear exist device list
        DEVICES_CONNECTED.clear()
        USED_ADB_PORT.clear()
        DEVICES_TEMP.clear()

        try {
            // loading confired device
            Config.loadDevices().forEach {
                DEVICES_TEMP[it.sn] = it
            }
        } catch (e: Exception) {
            XLog.e("AdbUtils", e)
        }

        // run list device command, this is a long-running operation. it will frozen ui
        val res = CmdUtils.execSync("adb devices -l")
        val lines = res.info.split(NEW_LINE)
        val devices = HashMap<String, Device>()

        lines.filter {
            !it.isBlank() && it.trim() !in LINE_NO_DEVICES
        }.mapNotNull {
            getDeviceFromLine(it)
        }.forEach { device ->
            // device does not connect, get device ip
            if (device.ip.isBlank()) {
                setIpAddress(device)
            }
            // device connected by wifi
            if (device.port.isNotBlank()) {
                if (device.ip !in DEVICES_CONNECTED) {
                    DEVICES_CONNECTED.add(device.ip)
                }
                DEVICES_TEMP[device.sn] = device
            }
            devices[device.sn] = device
        }
        devices.putAll(DEVICES_TEMP)
        try {
            // persistent connected devices.
            Config.saveDevice(DEVICES_TEMP.values.toList())
        }catch (e:Exception){
            XLog.e("AdbUtils", e)
        }
        return devices.values.toList()
    }

    fun disconnect(ip: String, port: String): CmdResult {
        return CmdUtils.execSync("adb disconnect $ip:$port")
    }

    fun connect(ip: String, port: String): CmdResult {
        if (port !in USED_ADB_PORT) {
            USED_ADB_PORT.add(port)
        }
        return CmdUtils.execSync("adb connect $ip${if (port.isBlank()) "" else ":$port"}")
    }

    fun turnTcp(device: Device, port: String): CmdResult {
        return CmdUtils.execSync("adb -s ${device.sn} tcpip $port")
    }

    fun screenRecord(device: Device, local: String, listener: CmdListener?) {
        val filePath = "$SCREEN_RECORD_PATH${System.currentTimeMillis()}.mp4"
        CmdUtils.adbShellSync(device, "screenrecord " +
                " --time-limit $SCREEN_RECORD_TIME_SECOND" +
                " --bit-rate $SCREEN_RECORD_BIT_RATE" +
                " --verbose " +
                filePath)
        pull(device, filePath, local, listener)
    }

    fun screenCap(device: Device, local: String, listener: CmdListener?) {
        val filePath = "$SCREEN_CAP_PATH${System.currentTimeMillis()}.png"
        CmdUtils.adbShell("screencap $filePath", listener)
        pull(device, filePath, local, listener)
    }

    fun installApk(device: Device, path: String, listener: CmdListener?) {
        CmdUtils.exec("adb -s ${device.sn} install $path", listener)
    }

    fun startServer() {
        CmdUtils.execSync("adb start-server")
    }

    fun killServer() {
        CmdUtils.execSync("adb kill-server")
    }

    fun start(device: Device, listener: CmdListener?) {
        CmdUtils.exec("adb -s ${device.sn} start", listener)
    }

    fun pull(device: Device, remote: String, local: String, listener: CmdListener?) {
        CmdUtils.exec("adb -s ${device.sn} pull $remote $local", listener)
    }

    fun isPortVailable(port: String): Boolean {
        return !USED_ADB_PORT.contains(port)
    }

    fun isIpConnected(ip: String): Boolean {
        return ip in DEVICES_CONNECTED
    }

    fun getTempDevices(): List<Device> {
        return DEVICES_TEMP.values.toList()
    }

    fun restartServer() {
        CmdUtils.exec("adb kill-server && adb devices")
    }

    private fun setIpAddress(device: Device) {
        val res = CmdUtils.adbShellSync(device, "ifconfig wlan0")
        val matcher = PATTERN_INET_ADDR.matcher(res.info)
        if (matcher.find()) {
            device.ip = matcher.group(1)
            device.broadcastAddress = matcher.group(9)
        }
    }

    private fun getDeviceFromLine(line: String): Device? {
        XLog.d("AdbUtils.getDeviceFromLine", line)
        val part = line.split(SPACE).filter {
            !it.isBlank()
        }
        val device = Device()
        device.sn = part[0]
        device.status = Device.Status.getStatus(part.elementAtOrElse(1) { "unknown" })
        device.modelName = part.elementAtOrElse(2) { "-:-" }.split(":")[1]
        device.model = part.elementAtOrElse(3) { "-:-" }.split(":")[1]

        // connected by wlan
        if (device.sn.contains(":")) {
            val tcp = device.sn.split(":")
            device.ip = tcp[0]
            device.port = tcp[1]
            USED_ADB_PORT.add(device.port)
            device.status = Device.Status.CONNECTED
        }
        return device
    }

    interface DeviceListListener {
        fun onDeviceList(list: List<Device>)
    }
}