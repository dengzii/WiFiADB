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

    lateinit var name: String
    lateinit var ip: String
    lateinit var model: String
    lateinit var modelName: String
    lateinit var port: String
    lateinit var status: STATUS

    constructor(name: String, model: String) : this() {
        this.name = name
        this.model = model
    }

    override fun toString(): String {
        return "Device{name=$name, model=$model}"
    }

    enum class STATUS {
        ONLINE, OFFLINE, DISCONNECT, UNKNOWN;
        companion object{
            fun getStatus(status : String) = when(status) {
                "device" -> ONLINE
                "disconnect" -> DISCONNECT
                "offline" -> OFFLINE
                else -> UNKNOWN
            }
        }
    }
}