package com.dengzii.plugin.adb

import com.intellij.ide.util.PropertiesComponent

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/10/10
 * desc   :
 * </pre>
 */

object Config {

    private val TAG = Config::class.java.simpleName
    private const val KEY_DEVICES = "com.dengzii.plugin.adb.devices"
    private const val KEY_DIALOG_CONFIG = "com.dengzii.plugin.adb.config.dialog"

    fun clear() {
        PropertiesComponent.getInstance().unsetValue(KEY_DEVICES)
    }

    fun loadDevices(): List<Device> {

        val pro = PropertiesComponent.getInstance()
        val deviceList = pro.getValues(KEY_DEVICES)
        val devices = ArrayList<Device>()
        deviceList?.forEach {
            val device = Device.fromSerialString(it)
            if (device != null) {
                devices.add(device)
            }
        }
        XLog.d("${TAG}.loadConfigDevice", devices.toString())
        return devices
    }

    fun saveDevice(devices: List<Device>) {

        XLog.d("$TAG.saveDevice", devices.toString())
        try {
            val pro = PropertiesComponent.getInstance()
            val serialList = devices.map { it.toSerialString() }.toTypedArray()
            pro.setValues(KEY_DEVICES, serialList)
        } catch (e: Throwable) {
            XLog.e("$TAG.saveDevice", e)
        }
    }

    fun saveDialogConfig(dialogConfig: DialogConfig) {
        PropertiesComponent.getInstance().setValue(KEY_DIALOG_CONFIG, dialogConfig.toSerialString())
    }

    fun loadDialogConfig(): DialogConfig {
        return DialogConfig.fromSerialString(PropertiesComponent.getInstance().getValue(KEY_DIALOG_CONFIG, ""))
    }
}

