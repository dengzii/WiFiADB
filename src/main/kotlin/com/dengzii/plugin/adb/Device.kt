package com.dengzii.plugin.adb

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

    fun getAttr(columnEnum: DialogConfig.ColumnEnum): Any {
        return when (columnEnum) {
            DialogConfig.ColumnEnum.SN -> serial
            DialogConfig.ColumnEnum.MODEL_NAME -> modelName
            DialogConfig.ColumnEnum.NAME -> model
            DialogConfig.ColumnEnum.IP -> ip
            DialogConfig.ColumnEnum.PORT -> port
            DialogConfig.ColumnEnum.STATUS -> status
            DialogConfig.ColumnEnum.MARK -> mark
            DialogConfig.ColumnEnum.OPERATE -> this
        }
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

        USB, CONNECTED, OFFLINE, DISCONNECTED, UNKNOWN, UNAUTHORIZED;

        companion object {
            fun getStatus(status: String?) = when (status) {
                "device" -> USB
                "disconnected" -> DISCONNECTED
                "offline" -> OFFLINE
                "unauthorized" -> UNAUTHORIZED
                else -> UNKNOWN
            }
        }
    }
}