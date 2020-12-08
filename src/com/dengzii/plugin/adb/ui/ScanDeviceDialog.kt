package com.dengzii.plugin.adb.ui

import com.dengzii.plugin.adb.tools.invokeLater
import com.dengzii.plugin.adb.tools.ui.ColumnInfo
import com.dengzii.plugin.adb.tools.ui.TableAdapter
import com.dengzii.plugin.adb.tools.ui.XDialog
import com.dengzii.plugin.adb.tools.ui.onClick
import com.dengzii.plugin.adb.utils.DeviceManager
import com.intellij.ide.ui.fullRow
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.layout.panel
import java.awt.BorderLayout
import java.awt.Component
import java.net.InetSocketAddress
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.*

class ScanDeviceDialog(private var callback: (InetSocketAddress) -> Unit) : XDialog("Scan Device [beta]") {

    private val table: JTable = JTable()
    private val tableData = mutableListOf<MutableList<Any?>>()
    private val columnInfo = mutableListOf<ColumnInfo<Any>>()
    private val tableAdapter = TableAdapter(tableData, columnInfo)

    private lateinit var buttonScan: JButton
    private lateinit var labelProgress: JLabel
    private lateinit var fieldTimeoutPing: JTextField
    private lateinit var fieldTimeoutAdb: JTextField
    private lateinit var fieldThreadNum: JTextField
    private lateinit var fieldPortStart: JTextField
    private lateinit var fieldPortEnd: JTextField

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
        table.rowHeight = 35
        table.columnSelectionAllowed = false
        table.rowSelectionAllowed = false
        tableAdapter.setup(table)

        contentPane = panel {
            row { label("") }
            row {
                label("Timeout").withLargeLeftGap()
                cell {
                    label("Ping:")
                    intTextField({ 1000 }, {}).apply {
                        fieldTimeoutPing = component
                    }
                    label("")

                    label("ADB:").withLargeLeftGap()
                    intTextField({ 1000 }, {}).apply {
                        fieldTimeoutAdb = component
                    }
                    label("")
                }
            }
            row {
                label("Thread Num").withLargeLeftGap()
                cell {
                    intTextField({ Runtime.getRuntime().availableProcessors() * 2 }, {}).apply {
                        fieldThreadNum = component
                    }
                    label("")

                    label("Available Processors: ")
                    label("${Runtime.getRuntime().availableProcessors()}")
                    label("")
                }
            }
            row {
                label("ADB Port").withLargeLeftGap()
                cell {
                    intTextField({ 5555 }, { println(it) }).apply {
                        fieldPortStart = component
                    }
                    label("-")
                    intTextField({ 5559 }, { }).apply {
                        fieldPortEnd = component
                    }
                    label("odd, gte 5555, lte 5585 ")
                }
            }
            row {
                cell {
                    label("").withLargeLeftGap()
                    button("Scan") {
                        scan()
                    }.apply {
                        buttonScan = component
                    }
                }
                cell {
                    label("Tap scan to start scan available devices.").apply {
                        labelProgress = component
                    }
                }
            }
            fullRow {
                panel("", JBScrollPane().apply {
                    table.fillsViewportHeight = true
                    setViewportView(table)
                })
            }

        }
    }

    private var scanExecutor: ExecutorService? = null
    private var tableInUpdate = AtomicBoolean(false)

    private fun scan() {
        when (buttonScan.text) {
            "Scan" -> {
                tableData.clear()
                tableAdapter.fireTableDataChanged()
                scanExecutor = DeviceManager.scanAvailableDevicesLan(
                        timeout = fieldTimeoutPing.text.toInt(),
                        adbTimeout = fieldTimeoutAdb.text.toInt(),
                        threadPoolSize = fieldThreadNum.text.toInt(),
                        ports = (fieldPortStart.text.toInt()..fieldPortEnd.text.toInt() step 1).toList()
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

    override fun onOpened() {
        super.onOpened()
        location = getLocationCenterOfScreen()
        tableAdapter.fireTableStructureChanged()
    }

}
