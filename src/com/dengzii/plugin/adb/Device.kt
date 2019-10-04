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

    private lateinit var name: String
    private lateinit var ip: String
    private lateinit var model: String
    private lateinit var modelName: String
    private lateinit var port: String
    private lateinit var status: STATUS

    constructor(name: String, model: String) : this() {
        this.name = name
        this.model = model
    }

    enum class STATUS {
        ONLINE, OFFLINE, DISCONNECT
    }
}