@file:Suppress("SpellCheckingInspection")

package com.dengzii.plugin.adb

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

    private const val CMD_LIST_DEVICES = "adb devices -l"
    private const val CMD_RESTART_ABD_SERVER = "adb kill-server && adb devices"
    private const val NO_DEVICE = "no device"

    private const val PERMISSION_DENIED = "Permission denied"
    private const val LIST_OF_DEVICES_ATTACHED = "List of devices attached"
    private const val SPACE = " "
    private const val NEW_LINE = "\n"

    private const val SCREEN_CAP_PATH = "/sdcard/"
    private const val SCREEN_RECORD_PATH = "/sdcard/"
    private const val SCREEN_RECORD_TIME_SECOND = 180
    private const val SCREEN_RECORD_BIT_RATE = 4000000

    private const val REGEX_IP = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}"
    private val PATTERN_INET_ADDR = Pattern.compile("inet addr:($REGEX_IP) {2}Bcast:($REGEX_IP)")

    val DEVICES = ArrayList<Device>()

    @JvmStatic
    fun main(args: Array<String>) {
        XLog.disable()
        getConnectedDeviceList(object : DeviceListListener {
            override fun onDeviceList(list: List<Device>) {
                println(list)
//                list[0].turnOnTcp(5555)
            }
        })
    }

    fun connect(device: Device, listener: CmdListener?) {
        CmdUtils.exec("adb connect ${device.ip}", listener)
    }

    fun turnTcp(device: Device, port: Int, listener: CmdListener?) {
        CmdUtils.exec("adb -s ${device.sn} tcpip $port", listener)
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

    fun start(device: Device, listener: CmdListener?) {
        CmdUtils.exec("adb -s ${device.sn} start", listener)
    }

    fun pull(device: Device, remote: String, local: String, listener: CmdListener?) {
        CmdUtils.exec("adb -s ${device.sn} pull $remote $local", listener)
    }


    fun getConnectedDeviceList(deviceListListener: DeviceListListener) {

        CmdUtils.exec(CMD_LIST_DEVICES, object : CmdListener {
            override fun onExecuted(success: Boolean, code: Int, msg: String) {
                if (msg.contains(NO_DEVICE)) {
                    deviceListListener.onDeviceList(listOf())
                    return
                }
                val lines = msg.split(NEW_LINE).filter {
                    !it.isBlank() && !it.contains(LIST_OF_DEVICES_ATTACHED)
                }
                val devices = ArrayList<Device>()
                lines.forEach {
                    val device = getDeviceFromLine(it)
                    if (device != null) {
                        if (device.ip.isBlank()) {
                            setIpAddress(device)
                        }
                        devices.add(device)
                    }
                }
                DEVICES.clear()
                DEVICES.addAll(devices)
                deviceListListener.onDeviceList(devices)
            }
        })
    }

    fun restartServer() {
        CmdUtils.exec(CMD_RESTART_ABD_SERVER)
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
        val part = line.split(SPACE).filter {
            !it.isBlank()
        }
        if (part.size < 4) {
            return null
        }
        val modelName = part[2].split(":")[1]
        val model = part[3].split(":")[1]
        val status = Device.STATUS.getStatus(part[1])
        val device = Device(part[0], model)
        device.status = status
        device.modelName = modelName

        if (device.sn.contains(":")) {
            val tcp = device.sn.split(":")
            device.ip = tcp[0]
            device.port = tcp[1]
        }
        return device
    }

    interface DeviceListListener {
        fun onDeviceList(list: List<Device>)
    }
}