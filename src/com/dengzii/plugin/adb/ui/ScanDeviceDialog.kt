package com.dengzii.plugin.adb.ui

import com.dengzii.plugin.adb.tools.ui.ColumnInfo
import com.dengzii.plugin.adb.tools.ui.TableAdapter
import com.dengzii.plugin.adb.tools.ui.XDialog
import com.dengzii.plugin.adb.tools.ui.onClick
import com.dengzii.plugin.adb.utils.DeviceManager
import com.intellij.ui.layout.panel
import java.awt.BorderLayout
import java.awt.Component
import java.util.concurrent.ExecutorService
import javax.swing.*

class ScanDeviceDialog(private var callback: (Map<String, Int>) -> Unit) : XDialog("Scan Device") {

    private val table: JTable = JTable()
    private val tableData = mutableListOf<MutableList<Any?>>()
    private val columnInfo = mutableListOf<ColumnInfo<Any>>()
    private val model = TableAdapter(tableData, columnInfo)

    private lateinit var buttonScan: JButton
    private lateinit var labelProgress: JLabel
    private lateinit var fieldTimeout: JTextField
    private lateinit var fieldThreadSize: JTextField
    private lateinit var fieldPortStart: JTextField
    private lateinit var fieldPortEnd: JTextField

    companion object {
        fun show(callback: (Map<String, Int>) -> Unit) {
            ScanDeviceDialog(callback).packAndShow()
        }
    }

    init {
        persistDialogState = false
        val c = object : ColumnInfo<Any>("Operate", true) {
            override val columnClass = Int::class.java

            override fun getEditComponent(item: Any?, row: Int, col: Int) = getButton(item)

            override fun getRendererComponent(item: Any?, row: Int, col: Int) = getButton(item)

            private fun getButton(value: Any?): Component {
                return JPanel().apply {
                    layout = BorderLayout(4, 4)
                    add(OperateButtonColumn.Button("Connect").apply {
                        onClick {
                            println(value)
                        }
                    }, BorderLayout.CENTER)
                }
            }
        }
        columnInfo.add(ColumnInfo("IP/PORT"))
        columnInfo.add(c)
        table.rowHeight = 45
        table.columnSelectionAllowed = false
        table.rowSelectionAllowed = false
        model.setup(table)

        contentPane = panel {
            row { label("") }
            row {
                label("Timeout").withLargeLeftGap()
                cell {
                    intTextField({ 2000 }, {}).apply {
                        fieldTimeout = component
                    }
                    label("")
                }
            }
            row {
                label("Thread Num").withLargeLeftGap()
                cell {
                    intTextField({ Runtime.getRuntime().availableProcessors() }, {}).apply {
                        fieldThreadSize = component
                    }
                    label("")
                }
            }
            row {
                label("ADB Port").withLargeLeftGap()
                cell {
                    intTextField({ 5555 }, { println(it) }, range = 5555..5561).apply {
                        fieldPortStart = component
//                        isEnabled = false
                    }
                    label("-")
                    intTextField({ 5561 }, { }, range = 5555..5561).apply {
                        fieldPortEnd = component
//                        isEnabled = false
                    }
                    label("4 ports. ")
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
            row {
                panel("", JScrollPane().apply {
                    table.fillsViewportHeight = true
                    setViewportView(table)
                })
            }

        }
    }

    private var scanExecutor: ExecutorService? = null
    private fun scan() {
        when (buttonScan.text) {
            "Scan" -> {
                scanExecutor = DeviceManager.scanAvailableDevicesLan(
                        timeout = fieldTimeout.text.toInt(),
                        threadPoolSize = fieldThreadSize.text.toInt()
                ) { progress, message, ip ->
                    labelProgress.text = "$progress%   $message"
                    if (ip.isNotEmpty()){
                        tableData.clear()
                        ip.forEach {
                            tableData.add(mutableListOf("${it.address.hostAddress}:${it.port}", it.port))
                        }
                        model.fireTableDataChanged()
                    }
                    if (progress == 100) {
                        buttonScan.text = "Scan"
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
        model.fireTableStructureChanged()
    }

}
