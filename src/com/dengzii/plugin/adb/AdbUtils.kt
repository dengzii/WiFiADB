package com.dengzii.plugin.adb

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/10/4
 * desc   :
</pre> *
 */
object AdbUtils {


    fun getConnectedDeviceList(): List<String> {

        CmdUtils.exec("adb devices -l", object : CmdUtils.CmdListener {
            override fun onExecuted(success: Boolean, code: Int, msg: String) {

            }
        })
        return listOf()
    }

    interface DeviceListListener {
        fun onDeviceList(): List<String>
    }
}