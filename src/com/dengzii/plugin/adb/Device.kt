package com.dengzii.plugin.adb

import com.dengzii.plugin.adb.utils.AdbUtils
import com.dengzii.plugin.adb.utils.CmdUtils
import com.dengzii.plugin.adb.utils.DeviceManager

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/10/4
 * desc   :
 * </pre>
 */
class Device() {

    var serial: String = ""
    var ip: String = ""
    var port: String = ""
    var model: String = ""
    var modelName: String = ""
    var status: Status = Status.UNKNOWN
    var broadcastAddress: String = ""
    var mark: String = ""
    var transportId: Int = -1

    companion object {

        fun fromSerialString(serialString: String): Device? {
            val args = serialString.split("#|#").toTypedArray()
            if (args.size != 8) {
                return null
            }
            return try {
                Device(*args)
            } catch (e: Exception) {
                XLog.e(e)
                null
            }
        }
    }

    private constructor(vararg arg: String) : this() {
        this.serial = arg[0]
        this.ip = arg[1]
        this.model = arg[2]
        this.modelName = arg[3]
        this.port = arg[4]
        this.status = Status.DISCONNECTED
        this.broadcastAddress = arg[6]
        this.mark = arg[7]
    }

    fun toSerialString(): String {
        return arrayOf(serial, ip, model, modelName, port, status.name, broadcastAddress, mark).joinToString("#|#")
    }

    fun turnOnTcp(port: String) {
        AdbUtils.tcpIp(port.toInt(), serial)
    }

    fun disconnect() {
        if (this.status == Status.CONNECTED && ip.isNotBlank() && port.isNotBlank()) {
            AdbUtils.disconnect(ip, port.toIntOrNull())
        }
    }

    fun connect(): CmdUtils.CmdResult {
        XLog.d("ip=$ip, port=$port, status=$status")
        if (status != Status.CONNECTED) {
            if (status == Status.ONLINE) { // connected by usb
                var p = 5555
                while (!DeviceManager.isPortAvailable(p.toString())) {
                    p += 2
                }
                port = p.toString()
                XLog.d("turn port $port")
                AdbUtils.tcpIp(p, serial)
            }
            val result = AdbUtils.connect(ip, port.toIntOrNull())
            if (result.success) {
                status = Status.CONNECTED
            }
            return result
        } else {
            XLog.d("device already connected.")
        }
        return CmdUtils.CmdResult(-1, "failed.", false)
    }

    override fun toString(): String {
        return "Device(sn='$serial', " +
                "ip='$ip', " +
                "model='$model', " +
                "modelName='$modelName', " +
                "port='$port', " +
                "status=$status, " +
                "broadcastAddress='$broadcastAddress'," +
                "mark=$mark)"
    }

    enum class Status {

        ONLINE, CONNECTED, OFFLINE, DISCONNECT, DISCONNECTED, UNKNOWN, UNAUTHORIZED;

        companion object {
            fun getStatus(status: String) = when (status) {
                "device" -> ONLINE
                "disconnect" -> DISCONNECT
                "disconnected" -> DISCONNECTED
                "offline" -> OFFLINE
                "unauthorized" -> UNAUTHORIZED
                else -> UNKNOWN
            }
        }
    }
}