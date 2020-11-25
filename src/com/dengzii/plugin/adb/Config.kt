package com.dengzii.plugin.adb

import com.dengzii.plugin.adb.utils.AdbUtils
import com.intellij.ide.util.PropertiesComponent

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/10/10
 * desc   :
 * </pre>
 */

object Config {

    private const val KEY_DEVICES_LIST = "com.dengzii.plugin.adb.devices"
    private const val KEY_MAIN_DIALOG_CONFIG = "com.dengzii.plugin.adb.config.dialog"
    private const val KEY_ABD_PATH = "com.dengzii.plugin.adb.config.adb"

    var jvm_test = false

    fun init() {
        AdbUtils.setAdbCommand(loadAdbPath())
    }

    fun clearDeviceList() {
        PropertiesComponent.getInstance().unsetValue(KEY_DEVICES_LIST)
    }

    fun loadAdbPath(): String? {
        val pro = PropertiesComponent.getInstance()
        return pro.getValue(KEY_ABD_PATH)
    }

    fun saveAdbPath(path: String) {
        PropertiesComponent.getInstance().setValue(KEY_ABD_PATH, path)
        AdbUtils.setAdbCommand(path)
    }

    fun loadDevices(): List<Device> {
        if (jvm_test) return emptyList()

        val pro = PropertiesComponent.getInstance()
        val deviceList = pro.getValues(KEY_DEVICES_LIST)
        val devices = ArrayList<Device>()
        deviceList?.forEach {
            val device = Device.fromSerialString(it)
            if (device != null) {
                devices.add(device)
            }
        }
        XLog.d(devices.toString())
        return devices
    }

    fun saveDevice(devices: List<Device>) {
        if (jvm_test) return

        XLog.d(devices.toString())
        try {
            val pro = PropertiesComponent.getInstance()
            val serialList = devices.map { it.toSerialString() }.toTypedArray()
            pro.setValues(KEY_DEVICES_LIST, serialList)
        } catch (e: Throwable) {
            XLog.e(e)
            PropertiesComponent.getInstance().unsetValue(KEY_DEVICES_LIST)
        }
    }

    fun saveDialogConfig(dialogConfig: DialogConfig) {
        PropertiesComponent.getInstance().setValue(KEY_MAIN_DIALOG_CONFIG, dialogConfig.toSerialString())
    }

    fun loadDialogConfig(): DialogConfig {
        return try {
            DialogConfig.fromSerialString(PropertiesComponent.getInstance().getValue(KEY_MAIN_DIALOG_CONFIG, ""))
        } catch (t: Throwable) {
            XLog.e(t)
            PropertiesComponent.getInstance().unsetValue(KEY_MAIN_DIALOG_CONFIG)
            DialogConfig()
        }
    }
}

