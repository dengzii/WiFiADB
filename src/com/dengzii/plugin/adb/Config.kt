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

    private const val KEY_DEVICES = "com.dengzii.plugin.adb.devices"

    fun clear() {
        PropertiesComponent.getInstance().unsetValue(KEY_DEVICES)
    }

    fun loadDevices(): List<Device> {

        val pro = PropertiesComponent.getInstance();
        val deviceList = pro.getValues(KEY_DEVICES)
        val devices = ArrayList<Device>()
        deviceList?.forEach {
            val device = Device.fromSerialString(it)
            if (device != null){
                devices.add(device)
            }
        }
        return devices
    }

    fun saveDevice(devices: List<Device>) {

        val pro = PropertiesComponent.getInstance();
        val serialList = devices.map { it.toSerialString() }.toTypedArray()
        pro.setValues(KEY_DEVICES, serialList)
    }

}

