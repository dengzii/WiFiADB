package com.dengzii.plugin.adb

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
    var status: STATUS = STATUS.UNKNOWN
    var broadcastAddress: String = ""

    companion object{
        const val TAG = "Device"
    }

    constructor(sn: String, model: String) : this() {
        this.sn = sn
        this.model = model
    }

    fun turnOnTcp(port: Int) {
        AdbUtils.turnTcp(this, port, null)
    }

    fun connect() {
        if (status != STATUS.ONLINE) {
            AdbUtils.connect(this, null)
        }else{
            XLog.d("$TAG.connect", "device already connected.")
        }
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

    enum class STATUS {

        ONLINE, OFFLINE, DISCONNECT, UNKNOWN, UNAUTHORIZED;

        companion object {
            fun getStatus(status: String) = when (status) {
                "device" -> ONLINE
                "disconnect" -> DISCONNECT
                "offline" -> OFFLINE
                "unauthorized" -> UNAUTHORIZED
                else -> UNKNOWN
            }
        }
    }
}