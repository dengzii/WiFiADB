package com.dengzii.plugin.adb.ui

import com.dengzii.plugin.adb.Config
import com.dengzii.plugin.adb.Device
import com.dengzii.plugin.adb.DialogConfig
import com.dengzii.plugin.adb.DialogConfig.ColumnEnum
import com.dengzii.plugin.adb.XLog
import com.dengzii.plugin.adb.tools.CheckLicense
import com.dengzii.plugin.adb.tools.invokeLater
import com.dengzii.plugin.adb.tools.ui.*
import com.dengzii.plugin.adb.ui.*
import com.dengzii.plugin.adb.tools.ui.*
import com.dengzii.plugin.adb.utils.AdbUtils
import com.dengzii.plugin.adb.utils.DeviceManager
import java.awt.Cursor
import java.util.*


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
        if (!AdbUtils.lookingForAdb()) {
            ConfigAdbDialog.createAndShow(true)
        }
        initDeviceTable()
        updateDevice()

        checkLicense()
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
                    deviceList.removeAt(row)
                    persistState()
                    updateDevice()
                }
            ))
        }
        initDeviceTableStructure()
    }

    private fun initDeviceTableStructure() {
        tableColumnInfo.clear()
        val cols = dialogConfig.col.map {
            if (it == ColumnEnum.OPERATE) {
                OperateButtonColumn.newInstance(it.name) { device ->
                    clicked(device)
                }
            } else {
                ColumnInfo.new(it.name, it == ColumnEnum.MARK)
            }.apply {
                this.columnWidth = dialogConfig.colWidth[it.name]
            }
        }
        tableColumnInfo.addAll(cols)
        tableAdapter.fireTableStructureChanged()
    }

    override fun persistState() {
        super.persistState()

        try {
            var markColIndex = -1
            tableAdapter.eachColumn { column, i ->
                val col = ColumnEnum.valueOf(column.headerValue.toString().toUpperCase(Locale.getDefault()))
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
            Config.saveDevice(
                deviceList.filter {
                    it.port.isNotBlank() || it.status != Device.Status.USB
                }
            )
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

        setHintLabel("Connecting...")
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

        setHintLabel("Disconnecting...")
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

    private fun setHintLabel(hint: String) {
        labelStatus.text = "<html>$hint</html>"
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
                    connectManual()
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
                    ConfigAdbDialog.createAndShow(false)
                }
                item("Custom Column") {
                    ConfigDialog.createAndShow {
                        dialogConfig = Config.loadDialogConfig()
                        initDeviceTableStructure()
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
            menu("Connect Manual") {
                onClick {
                    connectManual()
                }
            }
            menu("Help") {
                item("About") {
                    AboutDialog.show_()
                }
            }
        }
    }

    private fun connectManual() {
        ConnectDialog().show { save, ip, port ->
            setHintLabel("Connecting...")
            DeviceManager.connectDevice(ip, port) { b: Boolean, s: String ->
                if (b) {
                    setHintLabel("Success, updating...")
                    updateDevice()
                } else {
                    setHintLabel("Failed, $s")
                    updateDevice()
                    if (save) {
                        val d = Device()
                        d.serial = "unknown_$ip"
                        d.ip = ip
                        d.port = port.toString()
                        d.model = "unknown"
                        d.modelName = "unknown"
                        d.status = Device.Status.DISCONNECTED
                        DeviceManager.saveDevice(d)
                    }
                }
            }
        }
    }

    private fun checkLicense() {
        if (!CheckLicense.isLicensed()) {
            labelLicense.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            labelLicense.text = "You have not obtain the license yet. click to obtain license"
            labelLicense.onClick {
                CheckLicense.requestLicense("Register Plugin")
            }
        } else {
            contentPane.remove(labelLicense)
        }
    }
}