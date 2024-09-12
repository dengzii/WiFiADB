package com.dengzii.plugin.adb.utils

import com.dengzii.plugin.adb.Config
import com.dengzii.plugin.adb.Device
import com.dengzii.plugin.adb.XLog
import java.net.*
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern

/**
 * Device Manger.
 *
 * @author github.com/dengzii
 */
object DeviceManager {

    private const val REGEX_IP = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}"
    private val PATTERN_INET_ADDR = Pattern.compile("inet ($REGEX_IP)/24 brd ($REGEX_IP)")

    private val USED_ADB_PORT = Vector<String>()
    private val DEVICES_CONNECTED = Vector<String>()
    private val DEVICES_ALL = Hashtable<String, Device>()

    // Using for matches the cases commandline output are not device info.
    private val LINE_NO_DEVICES = arrayOf(
            "* daemon not running. starting it now on port 5037 *",
            "* daemon started successfully *",
            "List of devices attached",
            "adb server is out of date.  killing...")

    private fun isPortAvailable(port: String) = !USED_ADB_PORT.contains(port)

    fun saveDevice(device: Device){
        DEVICES_ALL[device.serial] = device
    }

    fun getDeviceList(callback: (success: Boolean?, message: String, devices: List<Device>) -> Unit) {

        // clear exist device list
        DEVICES_CONNECTED.clear()
        USED_ADB_PORT.clear()
        DEVICES_ALL.clear()

        // loading devices persisted
        Config.loadDevices().forEach {
            DEVICES_ALL[it.serial] = it
        }
        getConnectedDevices(callback)
    }

    fun connectDevice(device: Device, listener: (success: Boolean?, message: String) -> Unit) {
        XLog.d(device.toString())
        when (device.status) {
            Device.Status.CONNECTED -> listener.invoke(false, "Device is already connected.")
            Device.Status.USB -> {
                if (device.serial in DEVICES_ALL || device.ip in DEVICES_CONNECTED) {
                    listener.invoke(false, "Device is already connected. port: ${DEVICES_ALL[device.serial]?.port}")
                    return
                }
                var p = 5555
                while (!isPortAvailable(p.toString())) {
                    p += 2
                }
                listener.invoke(null, "Connecting to ${device.port}:$p")
                // returning on adb port
                AdbUtils.tcpIp(p, device.serial).execute { r1 ->
                    if (!r1.success) {
                        listener.invoke(false, r1.output)
                        return@execute
                    }
                    // connecting
                    AdbUtils.connect(device.ip, p).execute {
                        val msg = if (it.success) {
                            device.port = p.toString()
                            device.status = Device.Status.CONNECTED
                            "Connected to ${device.port}:$p"
                        } else {
                            it.success = false
                            it.output
                        }
                        listener.invoke(it.success, msg)
                    }
                }
            }
            Device.Status.DISCONNECTED -> {
                AdbUtils.connect(device.ip, device.port.toIntOrNull()).execute {
                    val msg = if (it.success) {
                        device.status = Device.Status.CONNECTED
                        "Connected to ${device.port}:${device.port}"
                    } else {
                        it.output
                    }
                    listener.invoke(it.success, msg)
                }
            }
            else -> listener.invoke(false, "Cannot connect device, status=${device.status}.")
        }
    }

    fun disconnectDevice(device: Device, listener: (success: Boolean?, message: String) -> Unit) {
        XLog.d(device.toString())
        when (device.status) {
            Device.Status.DISCONNECTED -> listener.invoke(false, "Device is already disconnected.")
            Device.Status.USB,
            Device.Status.CONNECTED -> {
                if (device.port.isBlank()) {
                    AdbUtils.disconnect(null)
                } else {
                    AdbUtils.disconnect(device.ip, device.port.toIntOrNull())
                }.execute {
                    val msg = if (it.success) {
                        device.status = Device.Status.DISCONNECTED
                        "Disconnected device ${device.modelName}-${device.ip}"
                    } else {
                        it.output
                    }
                    listener.invoke(it.success, msg)
                }
            }
            else -> listener.invoke(false, "Cannot disconnect device, status=${device.status}.")
        }
    }

    fun connectDevice(ip: String, port: Int, listener: (success: Boolean, message: String) -> Unit) {
        AdbUtils.connect(ip, port).execute { res ->
            listener.invoke(res.output.contains("connected to ${ip}:${port}"), res.output)
        }
    }

//    fun touchDevice(ip: String, listener: (available: Boolean, msg: String) -> Unit) {
//        Thread {
//            val inetAddress = InetAddress.getByName(ip)
//            try {
//                inetAddress.isReachable(1000)
//            } catch (e: java.lang.Exception) {
//                listener.invoke(false, "host $ip is unreachable.")
//                return@Thread
//            }
//            val telnetClient = TelnetClient()
//            telnetClient.connectTimeout = 1000
//            for (p in 5555..5561 step 1) {
//                try {
//                    telnetClient.connect(inetAddress, p)
//                    listener.invoke(true, "${ip}:$p is available.")
//                    return@Thread
//                } catch (e: java.lang.Exception) {
//
//                }
//            }
//            listener.invoke(false, "the adb tcp/ip port is closed.")
//        }.start()
//    }

    private val scanned = AtomicInteger(0)
    private val scanDeviceIpList = Vector<InetSocketAddress>()

//    fun scanAvailableDevicesLan(
//            timeout: Int = 2000,
//            adbTimeout: Int = 1000,
//            ports: List<Int> = listOf(5555, 5557, 5559),
//            threadPoolSize: Int = Runtime.getRuntime().availableProcessors() * 2,
//            scanIp: List<InetAddress>? = null,
//            callback: (progress: Int, message: String, ip: List<InetSocketAddress>) -> Unit
//    ): ExecutorService {
//        val executor = Executors.newFixedThreadPool(threadPoolSize)
//        val subnetIp = scanIp ?: getAllSubnetIp(InetAddress.getLocalHost())
//        val availableIpSize = subnetIp.size
//        scanned.set(0)
//        scanDeviceIpList.clear()
//        val telnetClient = TelnetClient()
//        telnetClient.connectTimeout = adbTimeout
//        val logBuilder = StringBuffer()
//        callback.invoke(0, "scanning...", emptyList())
//        subnetIp.forEach { inetAddress ->
//            executor.submit {
//                if (Thread.interrupted()) {
//                    return@submit
//                }
//                val reachable = inetAddress.isReachable(timeout)
//                var msg = ""
//                if (reachable) {
//                    var s = "${inetAddress.hostAddress} "
//                    for (i in ports) {
//                        try {
//                            msg = "${inetAddress.hostAddress}:${i}"
//                            s = s.plus("$i ")
//                            val socketAddress = InetSocketAddress(inetAddress.hostAddress, i)
//                            telnetClient.connect(inetAddress, i)
//                            scanDeviceIpList.add(socketAddress)
//                            break
//                        } catch (e: Exception) {
//                        }
//                    }
//                    logBuilder.append("${s}\n")
//                } else {
//                    msg = "${inetAddress.hostAddress} is unreachable."
//                }
//                val progress = (scanned.incrementAndGet().toFloat() / availableIpSize.toFloat()) * 100
//                if (scanned.get() >= availableIpSize) {
//                    msg = "scan finish, ${scanDeviceIpList.size} device may available."
//                    XLog.d(logBuilder.toString())
//                    callback.invoke(progress.toInt(), msg, scanDeviceIpList)
//                    executor.shutdownNow()
//                }
//                if (!Thread.interrupted()) {
//                    callback.invoke(progress.toInt(), msg, scanDeviceIpList)
//                }
//            }
//        }
//        return executor
//    }

    fun getAllSubnetIp(inetAddress: InetAddress): List<InetAddress> {
        val address = inetAddress.address
        val bitMask = NetworkInterface.getByInetAddress(inetAddress)
                .interfaceAddresses[0].networkPrefixLength
        val netmask = (0xff_ff_ff_ff shl (32 - bitMask)).toInt()
        val ip = address[0].toLong() shl 24 or
                (address[1].toLong() shl 16 and (0xff shl 16)) or
                (address[2].toLong() shl 8 and (0xff shl 8)) or
                (address[3].toLong() and 0xff)
        val startIp = ip and netmask
        val hosts = mutableListOf<Long>()
        for (i in 1L until netmask.inv()) {
            val h = startIp or i
            if (h == ip) {
                continue
            }
            hosts.add(startIp or i)
        }
        return hosts.map { InetAddress.getByName(ipToString(it)) }
    }

    private fun ipToString(address: Long): String {
        return (address ushr 24 and 0xFF).toString() + "." +
                (address ushr 16 and 0xFF) + "." +
                (address ushr 8 and 0xFF) + "." +
                (address and 0xFF)
    }

    private fun getConnectedDevices(callback: (success: Boolean?, message: String, devices: List<Device>) -> Unit) {
        callback.invoke(null, "Getting device list...", emptyList())
        AdbUtils.listDevices(true).execute { res ->
            val devices = mutableMapOf<String, Device>()
            if (!res.success) {
                callback.invoke(false, res.output, emptyList())
                return@execute
            }
            val lines = res.output.split("\n")
            lines.filter {
                it.isNotBlank() && it.trim() !in LINE_NO_DEVICES
            }.mapNotNull {
                try {
                    getDeviceFromLine(it)
                } catch (e: Exception) {
                    null
                }
            }.forEach { device ->
                if (device.port.isNotBlank()) {
                    USED_ADB_PORT.add(device.port)
                }
                // device does not connect, get device ip
                if (device.ip.isBlank()) {
                    callback.invoke(null, "Getting device info...", emptyList())
                    device.ip = getIpAddress(device.serial)
                    if (device.ip.isBlank()) {
                        callback.invoke(null, "Unable to get device IP", emptyList())
                    }
                }
                // device connected by wifi
                if (device.port.isNotBlank()) {
                    if (device.ip !in DEVICES_CONNECTED) {
                        DEVICES_CONNECTED.add(device.ip)
                    }
                }
                DEVICES_ALL[device.serial] = device
                device.mark = DEVICES_ALL.getOrDefault(device.serial, device).mark
                devices[device.serial] = device
            }
            // persistent connected devices.
            Config.saveDevice(DEVICES_ALL.values.filter { it.port.isNotBlank() })
            callback.invoke(true, "All devices are listed.", DEVICES_ALL.values.toList())
        }
    }

    private fun getIpAddress(serial: String): String {
        try {
            val res = AdbUtils.adbShell("ip addr show wlan0", serial = serial).execute()
            if (!res.success) {
                return ""
            }
            val matcher = PATTERN_INET_ADDR.matcher(res.output)
            if (matcher.find()) {
                return matcher.group(1)
            }
        } catch (e: Exception) {
            XLog.e(e)
        }
        return ""
    }

    private fun getDeviceFromLine(line: String): Device? {

        val part = line.split(" ").filter {
            it.isNotBlank()
        }
        val device = Device()
        device.serial = part[0]
        device.status = Device.Status.getStatus(part.getOrNull(1))

        device.modelName = part[2].split(":")[1]
        device.model = part[3].split(":")[1]
        device.transportId = part[4].split(":")[1].toIntOrNull() ?: -1

        // connected by wlan
        if (device.serial.contains(":")) {
            val tcp = device.serial.split(":")
            device.ip = tcp[0]
            device.port = tcp[1]
            device.status = Device.Status.CONNECTED
        }
        return device
    }

    private infix fun Long.and(netmask: Int): Long {
        return this and netmask.toLong()
    }
}
