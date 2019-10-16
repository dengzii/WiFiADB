package com.dengzii.plugin.adb

import com.dengzii.plugin.adb.utils.AdbUtils
import com.dengzii.plugin.adb.utils.CmdListener
import com.dengzii.plugin.adb.utils.CmdResult
import com.dengzii.plugin.adb.utils.CmdUtils

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/10/4
 * desc   :
 * </pre>
 */
class Device() {

    var sn: String = ""
    var ip: String = ""
    var model: String = ""
    var modelName: String = ""
    var port: String = ""
    var status: Status = Status.UNKNOWN
    var broadcastAddress: String = ""

    companion object {
        private const val TAG = "Device"

        fun fromSerialString(serialString: String): Device? {
            val args = serialString.split("#|#").toTypedArray()
            if (args.size != 7) {
                return null
            }
            return try {
                Device(*args)
            } catch (e: Exception) {
                XLog.e(TAG, e)
                null
            }
        }
    }

    private constructor(vararg arg: String) : this() {
        this.sn = arg[0]
        this.ip = arg[1]
        this.model = arg[2]
        this.modelName = arg[3]
        this.port = arg[4]
        this.status = Status.DISCONNECTED
        this.broadcastAddress = arg[6]
    }

    fun toSerialString(): String {
        return arrayOf(sn, ip, model, modelName, port, status.name, broadcastAddress).joinToString("#|#")
    }

    fun turnOnTcp(port: String) {
        AdbUtils.turnTcp(this, port)
    }

    fun disconnect() {
        if (this.status == Status.CONNECTED && ip.isNotBlank() && port.isNotBlank()) {
            AdbUtils.disconnect(ip, port)
        }
    }

    fun connect(): CmdResult? {
        if (status != Status.CONNECTED && !AdbUtils.isIpConnected(ip)) {
            var p = 5555
            while (!AdbUtils.isPortVailable(p.toString())) {
                p += 2
            }
            port = p.toString()
            AdbUtils.turnTcp(this, port)
            status = Status.CONNECTED
            return AdbUtils.connect(ip, port)
        } else {
            XLog.d("$TAG.connect", "device already connected.")
        }
        return null
    }

    fun installApk(path: String) {
        AdbUtils.installApk(this, path, null)
    }

    fun screenRecord(path: String) {
        AdbUtils.screenRecord(this, path, null)
    }

    fun screenCap(path: String) {
        AdbUtils.screenCap(this, path, null)
    }

    fun restart() {
        AdbUtils.start(this, null)
    }

    private fun shell(cmd: String, listener: CmdListener? = null) {
        CmdUtils.adbShell(this, cmd, listener)
    }

    override fun toString(): String {
        return "Device(sn='$sn', " +
                "ip='$ip', " +
                "model='$model', " +
                "modelName='$modelName', " +
                "port='$port', " +
                "status=$status, " +
                "broadcastAddress='$broadcastAddress')"
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