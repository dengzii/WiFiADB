package com.dengzii.plugin.adb.ui

import com.dengzii.plugin.adb.Config
import com.dengzii.plugin.adb.Device
import com.dengzii.plugin.adb.DialogConfig
import com.dengzii.plugin.adb.DialogConfig.ColumnEnum
import com.dengzii.plugin.adb.XLog
import com.dengzii.plugin.adb.tools.invokeLater
import com.dengzii.plugin.adb.tools.ui.*
import com.dengzii.plugin.adb.utils.AdbUtils
import com.dengzii.plugin.adb.utils.DeviceManager

/**
 * WiFiADB Tools main dialog.
 *
 * @author https://github.com/dengzii
 */
class MainDialog : MainDialogDesign() {

    private var dialogConfig = DialogConfig.INSTANCE

    private val deviceList = mutableListOf<Device>()
    private val tableColumnInfo = mutableListOf<ColumnInfo<Any>>()
    private val tableData = mutableListOf<MutableList<Any?>>()
    private var tableAdapter = TableAdapter(tableData, tableColumnInfo)

    override fun onOpened() {
        super.onOpened()

        initMenuBar()
        buttonRefresh.onClick {
            updateDevice()
        }
        if (!AdbUtils.isAdbAvailable()) {
            ConfigAdbDialog.createAndShow()
        }
        initDeviceTable()
        updateDevice()
    }

    private fun setHintLabel(status: String) {
        labelStatus.text = status
    }

    @Synchronized
    private fun updateDevice() {
        setHintLabel("Refreshing, please wait...")
        deviceList.clear()
        tableData.clear()
        DeviceManager.getDeviceList { success, message, devices ->
            synchronized(deviceList) {
                if (success == true) {
                    deviceList.addAll(devices)
                    deviceList.forEach { device ->
                        val rowList = mutableListOf<Any?>()
                        dialogConfig.col.forEach { c ->
                            rowList.add(device.getAttr(c))
                        }
                        tableData.add(rowList)
                    }
                    invokeLater {
                        tableAdapter.fireTableStructureChanged()
                        tableAdapter.fireTableDataChanged()
                    }
                }
                invokeLater {
                    setHintLabel(message)
                }
            }
        }

    }

    private fun initDeviceTable() {

        deviceTable.rowHeight = DialogConfig.ROW_HEIGHT
        deviceTable.columnSelectionAllowed = false
        deviceTable.rowSelectionAllowed = false
        tableAdapter.setup(deviceTable)

        deviceTable.onRightMouseButtonClicked {
            PopMenuUtils.show(it, hashMapOf(
                    "Delete" to {
                        val row = deviceTable.rowAtPoint(it.point)
                        tableData.removeAt(row)
                        persistState()
                        updateDevice()
                    }
            ))
        }
        initDeviceTableStructure()
    }

    private fun initDeviceTableStructure() {
        tableColumnInfo.clear()
        tableColumnInfo.addAll(dialogConfig.col.map {
            val c: ColumnInfo<Any> = if (it == ColumnEnum.OPERATE) {
                OperateButtonColumn(it.name, this::clicked)
            } else {
                ColumnInfo(it.name, it == ColumnEnum.MARK)
            }
            c.columnWidth = dialogConfig.colWidth[it.name]
            c
        })
        tableAdapter.fireTableStructureChanged()
    }

    override fun persistState() {
        super.persistState()

        try {
            var markColIndex = -1
            tableAdapter.eachColumn { column, i ->
                val col = ColumnEnum.valueOf(column.headerValue.toString().toUpperCase())
                if (col == ColumnEnum.MARK) {
                    markColIndex = i
                }
                dialogConfig.colWidth[col.name] = column.width
                dialogConfig.col.remove(col)
                dialogConfig.col.add(col)
            }
            Config.saveDialogConfig(dialogConfig)
            if (markColIndex >= 0) {
                for (row in tableData.indices) {
                    deviceList[row].mark = deviceTable.getValueAt(row, markColIndex).toString()
                }
            }
            Config.saveDevice(deviceList)
        } catch (t: Throwable) {
            XLog.e(t)
        }
    }

    private fun clicked(device: Device) {

        when (device.status) {
            Device.Status.CONNECTED, Device.Status.OFFLINE -> {
                disconnect(device)
            }
            Device.Status.USB, Device.Status.DISCONNECTED -> {
                connect(device)
            }
            else -> {
            }
        }
    }

    private var wait = false
    private fun connect(device: Device) {
        if (wait) return
        wait = true

        DeviceManager.connectDevice(device) { success, message ->
            invokeLater {
                setHintLabel(message)
                if (success == true) {
                    updateDevice()
                }
                if (success != null) {
                    wait = false
                }
            }
        }
    }

    private fun disconnect(device: Device) {
        if (wait) return
        wait = true

        DeviceManager.disconnectDevice(device) { success, message ->
            invokeLater {
                setHintLabel(message)
                if (success == true) {
                    updateDevice()
                }
                if (success != null) {
                    wait = false
                }
            }
        }
    }

    private fun initMenuBar() {
        jMenuBar = XMenuBar {
            menu("Main") {
                item("Log") {
                    LogDialog().show(XLog.getAllLog())
                }
                item("Clean All Device") {
                    Config.clearDeviceList()
                    updateDevice()
                }
                item("Refresh Device") {
                    buttonRefresh.doClick()
                }
                item("Connect Manual") {
                    ConnectDialog().show { updateDevice() }
                }
                item("Scan Device [beta]") {
                    ScanDeviceDialog.show { address ->
                        setHintLabel("Connecting to ${address.address.hostAddress}:${address.port}")
                        DeviceManager.connectDevice(address.address.hostAddress, address.port) { success, msg ->
                            invokeLater {
                                if (success) {
                                    updateDevice()
                                }
                                setHintLabel(msg)
                            }
                        }
                    }
                }
                item("Exit") {
                    dispose()
                }
            }
            menu("ADB") {
                item("Restart Service") {
                    AdbUtils.restartServer()
                    updateDevice()
                }
                item("Kill Service") {
                    AdbUtils.killServer()
                    updateDevice()
                }
                item("Start Service") {
                    AdbUtils.startServer()
                    updateDevice()
                }
            }
            menu("Settings") {
                item("Configure Adb Path") {
                    ConfigAdbDialog.createAndShow()
                }
                item("Custom Column") {
                    ConfigDialog.createAndShow {
                        tableAdapter.fireTableStructureChanged()
                        updateDevice()
                    }
                }
                item("Reset To Default") {
                    Config.saveDialogConfig(DialogConfig())
                    dialogConfig = Config.loadDialogConfig()
                    initDeviceTableStructure()
                    updateDevice()
                }
            }
            menu("Help") {
                item("About") {
                    AboutDialog().packAndShow()
                }
            }
        }
    }
}