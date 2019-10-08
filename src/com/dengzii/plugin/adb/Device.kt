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

    lateinit var sn: String
    lateinit var ip: String
    lateinit var model: String
    lateinit var modelName: String
    lateinit var port: String
    lateinit var status: STATUS
    lateinit var broadcastAddress: String

    constructor(name: String, model: String) : this() {
        this.sn = name
        this.model = model
    }

    override fun toString(): String {
        return "Device{name=$sn, model=$model}"
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