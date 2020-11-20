package com.dengzii.plugin.adb.utils

import com.dengzii.plugin.adb.Config
import com.dengzii.plugin.adb.Device
import com.dengzii.plugin.adb.XLog
import java.util.regex.Pattern

/**
 * Utils about ADB.
 *
 * @author github.com/dengzii
 */
object DeviceManager {

    private const val SPACE = " "
    private const val NEW_LINE = "\n"

    private const val REGEX_IP = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}"
    private val PATTERN_INET_ADDR = Pattern.compile("inet ($REGEX_IP)/24 brd ($REGEX_IP)")

    private val DEVICES_CONNECTED = ArrayList<String>()
    private val USED_ADB_PORT = mutableListOf<String>()
    private val DEVICES_TEMP = HashMap<String, Device>()

    // Using for matches the cases commandline output are not device info.
    private val LINE_NO_DEVICES = arrayOf(
            "* daemon not running. starting it now on port 5037 *",
            "* daemon started successfully *",
            "List of devices attached",
            "adb server is out of date.  killing...")

    fun isPortAvailable(port: String) = !USED_ADB_PORT.contains(port)

    fun getDeviceList(): List<Device> {

        // clear exist device list
        DEVICES_CONNECTED.clear()
        USED_ADB_PORT.clear()
        DEVICES_TEMP.clear()
        DEVICES_TEMP.putAll(loadConfigDevice())

        DEVICES_TEMP.putAll(getConnectedDevices())

        // persistent connected devices.
        Config.saveDevice(DEVICES_TEMP.values.toList())
        return DEVICES_TEMP.values.toMutableList()
    }

    fun getConnectedDevices(): MutableMap<String, Device> {

        val devices = mutableMapOf<String, Device>()

        // run list device command, this is a long-running operation. it will frozen ui
        val res = AdbUtils.listDevices()
        val lines = res.execute().output.split(NEW_LINE)

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
                device.mark = DEVICES_TEMP.getOrDefault(device.serial, device).mark
                DEVICES_TEMP[device.serial] = device
            }
            devices[device.serial] = device
        }

        return devices
    }

    private fun setIpAddress(device: Device) {
        val res = AdbUtils.adbShell("ip addr show wlan0", device.serial)
        try {
            val matcher = PATTERN_INET_ADDR.matcher(res.execute().output)
            if (matcher.find()) {
                device.ip = matcher.group(1)
//            device.broadcastAddress = matcher.group(9)
            }
        } catch (e: Exception) {
            XLog.e(e)
        }
    }

    private fun getDeviceFromLine(line: String): Device? {
        XLog.d("AdbUtils.getDeviceFromLine", line)
        val part = line.split(SPACE).filter {
            !it.isBlank()
        }
        val device = Device()
        device.serial = part[0]
        device.status = Device.Status.getStatus(part.elementAtOrElse(1) { "unknown" })
        device.modelName = part.elementAtOrElse(2) { "-:-" }.split(":").elementAtOrElse(1) { "-" }
        device.model = part.elementAtOrElse(3) { "-:-" }.split(":").elementAtOrElse(1) { "-" }

        // connected by wlan
        if (device.serial.contains(":")) {
            val tcp = device.serial.split(":")
            device.ip = tcp[0]
            device.port = tcp[1]
            USED_ADB_PORT.add(device.port)
            device.status = Device.Status.CONNECTED
        }
        return device
    }

    private fun loadConfigDevice(): MutableMap<String, Device> {
        val device = mutableMapOf<String, Device>()
        try {
            // loading confired device
            Config.loadDevices().forEach {
                device[it.serial] = it
            }
        } catch (e: Exception) {
            XLog.e(e)
        }
        return device
    }
}