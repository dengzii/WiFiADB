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

    private const val CMD_LIST_DEVICES = "adb devices -l"

    private const val LIST_OF_DEVICES_ATTACHED = "List of devices attached"
    private const val SPACE = " "
    private const val NEW_LINE = "\n"

    @JvmStatic
    fun main(args: Array<String>) {
        getConnectedDeviceList(object :DeviceListListener{
            override fun onDeviceList(list:  List<Device>) {
                print(list)
            }
        })
    }

    fun getConnectedDeviceList(deviceListListener: DeviceListListener){

        CmdUtils.exec(CMD_LIST_DEVICES, object : CmdUtils.CmdListener {
            override fun onExecuted(success: Boolean, code: Int, msg: String) {
                val lines = msg.split(NEW_LINE).filter {
                    !it.isBlank() && !it.contains(LIST_OF_DEVICES_ATTACHED)
                }
                val devices = ArrayList<Device>()
                lines.forEach {
                    devices.add(getDeviceFromLine(it))
                }
                deviceListListener.onDeviceList(devices)
            }
        })
    }

    private fun getDeviceFromLine(line : String) : Device{
        val part = line.split(SPACE).filter {
            !it.isBlank()
        }
        if (part.size < 4){
            return Device("","")
        }
        val model = part[2].split(":")[1]
        val modelName = part[3].split(":")[1]
        val status = Device.STATUS.getStatus(part[1])
        val device =  Device(part[0], model)
        device.status = status
        device.modelName = modelName
        return device
    }

    interface DeviceListListener {
        fun onDeviceList(list: List<Device>)
    }
}