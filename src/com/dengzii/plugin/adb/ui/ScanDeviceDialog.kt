package com.dengzii.plugin.adb.ui

import com.dengzii.plugin.adb.tools.invokeLater
import com.dengzii.plugin.adb.tools.ui.ColumnInfo
import com.dengzii.plugin.adb.tools.ui.TableAdapter
import com.dengzii.plugin.adb.tools.ui.onClick
import com.dengzii.plugin.adb.utils.DeviceManager
import java.awt.BorderLayout
import java.awt.Component
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JPanel

class ScanDeviceDialog(private var callback: (InetSocketAddress) -> Unit) : ScanDialogDesign() {

    private val tableData = mutableListOf<MutableList<Any?>>()
    private val columnInfo = mutableListOf<ColumnInfo<Any>>()
    private val tableAdapter = TableAdapter(tableData, columnInfo)
    private val subnetIp: List<InetAddress>

    companion object {
        fun show(callback: (InetSocketAddress) -> Unit) {
            ScanDeviceDialog(callback).packAndShow()
        }
    }

    init {
        persistDialogState = false
        val c = object : ColumnInfo<Any>("Operate", true) {
            override val columnClass = InetSocketAddress::class.java

            override fun getEditComponent(item: Any?, row: Int, col: Int) = getButton(item)

            override fun getRendererComponent(item: Any?, row: Int, col: Int) = getButton(item)

            private fun getButton(value: Any?): Component {
                return JPanel().apply {
                    layout = BorderLayout(4, 4)
                    add(OperateButtonColumn.Button("Connect").apply {
                        onClick {
                            if (value is InetSocketAddress) {
                                hideAndDispose()
                                callback.invoke(value)
                            }
                        }
                    }, BorderLayout.CENTER)
                }
            }
        }
        columnInfo.add(ColumnInfo("IP/PORT"))
        columnInfo.add(c)
        tableResult.rowHeight = 35
        tableResult.columnSelectionAllowed = false
        tableResult.rowSelectionAllowed = false
        tableAdapter.setup(tableResult)

        val localhost = InetAddress.getLocalHost()
        val bitMask = NetworkInterface.getByInetAddress(localhost)
                .interfaceAddresses[0].networkPrefixLength
        subnetIp = DeviceManager.getAllSubnetIp(localhost)

        fieldIpStart.text = (subnetIp.first().address[3].toLong() and 0xff).toString()
        fieldIpEnd.text = (subnetIp.last().address[3].toLong() and 0xff).toString()
        labelIpStart.text = subnetIp.first().hostAddress.removeSuffix(fieldIpStart.text)
        labelIpEnd.text = subnetIp.last().hostAddress.removeSuffix(fieldIpEnd.text)
        labelIp.text = "${localhost.hostAddress}/${bitMask}"

        fieldThreadNum.text = Runtime.getRuntime().availableProcessors().toString()
        labelProcessor.text = "Available Processors : ${fieldThreadNum.text}"
        labelProgress.text = "Tap scan to start scan available device."

        buttonScan.onClick {
            scan()
        }
    }

    private var scanExecutor: ExecutorService? = null
    private var tableInUpdate = AtomicBoolean(false)

    private fun scan() {

        val ips = mutableListOf<InetAddress>()
        val progression = fieldIpStart.text.trim().toInt()..fieldIpEnd.text.trim().toInt()
        progression.forEach {
            ips.add(InetAddress.getByName("${labelIpStart.text}$it"))
        }

        when (buttonScan.text) {
            "Scan" -> {
                tableData.clear()
                tableAdapter.fireTableDataChanged()
                scanExecutor = DeviceManager.scanAvailableDevicesLan(
                        timeout = fieldTimeoutPing.text.toInt(),
                        adbTimeout = filedTimeoutAdb.text.toInt(),
                        threadPoolSize = fieldThreadNum.text.toInt(),
                        ports = (fieldPortStart.text.toInt()..fieldPortEnd.text.toInt() step 1).toList(),
                        scanIp = ips
                ) { progress, message, ip ->
                    invokeLater {
                        labelProgress.text = "$progress%   $message"
                        if (ip.isNotEmpty() && !tableInUpdate.getAndSet(true)) {
                            tableData.clear()
                            ip.forEach {
                                tableData.add(mutableListOf("${it.address.hostAddress}:${it.port}", it))
                            }
                            tableAdapter.fireTableDataChanged()
                            tableInUpdate.set(false)
                        }
                        if (progress == 100) {
                            buttonScan.text = "Scan"
                        }
                    }
                }
                buttonScan.text = "Stop"
            }
            "Stop" -> {
                scanExecutor?.shutdownNow()
                scanExecutor = null
                buttonScan.text = "Scan"
            }
        }
    }

    override fun pack() {
        super.pack()
        location = getLocationCenterOfScreen()
        tableAdapter.fireTableStructureChanged()
    }
}
