package com.dengzii.plugin.adb.ui

import com.dengzii.plugin.adb.Config
import com.dengzii.plugin.adb.Device
import com.dengzii.plugin.adb.DialogConfig
import com.dengzii.plugin.adb.DialogConfig.COL
import com.dengzii.plugin.adb.XLog
import com.dengzii.plugin.adb.tools.NotificationUtils
import com.dengzii.plugin.adb.tools.ui.PopMenuUtils
import com.dengzii.plugin.adb.tools.ui.XMenuBar
import com.dengzii.plugin.adb.tools.ui.onRightMouseButtonClicked
import com.dengzii.plugin.adb.utils.AdbUtils
import com.dengzii.plugin.adb.utils.DeviceManager
import java.awt.Dimension
import java.awt.Toolkit

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2020/11/19
 * desc   : WiFiADB Tools main dialog.
 * </pre>
 */
class RealAdbDialog : AdbDialog() {

    private var deviceTableModel = DeviceTableModel()
    private var dialogConfig = DialogConfig.INSTANCE
    private var deviceList = mutableListOf<Device>()

    override fun onOpened() {
        super.onOpened()

        initMenuBar()
        restoreDialogState()
        buttonRefresh.addActionListener {
            updateDeviceTable()
        }
        if (!AdbUtils.isAdbAvailable()) {
            ConfigAdbDialog.createAndShow()
        }
        initDeviceTable()
    }

    override fun onClosing() {
        super.onClosing()
        persistDialogStateAndDevicesInfo()
    }

    fun setHintLabel(status: String?) {
        labelStatus.text = status
    }

    fun updateDeviceTable() {
        setHintLabel("Refreshing, please wait...")
        Thread { updateDeviceTableOnUi() }.start()
    }

    fun updateDeviceTableOnUi() {
        deviceList = DeviceManager.getDeviceList().toMutableList()
        deviceTableModel.setData(deviceList)
        deviceTableModel.fireTableStructureChanged()
        updateDeviceTableColumn()
        setHintLabel("Refresh complete")
    }

    private fun restoreDialogState() {
        setLocation(dialogConfig.x, dialogConfig.y)
        contentPane.preferredSize = Dimension(dialogConfig.width, dialogConfig.height)
    }

    private fun initDeviceTable() {
        deviceTableModel.columnCount = dialogConfig.col.size
        deviceTable.model = deviceTableModel
        deviceTable.rowHeight = DialogConfig.ROW_HEIGHT
        deviceTable.columnSelectionAllowed = false
        deviceTable.rowSelectionAllowed = false
        deviceTable.onRightMouseButtonClicked {
            PopMenuUtils.show(it, hashMapOf(
                    "Delete" to {
                        val row = deviceTable.rowAtPoint(it.point)
                        deviceList.removeAt(row)
                        persistDialogStateAndDevicesInfo()
                        updateDeviceTableOnUi()
                    }
            ))
        }
        updateDeviceTableColumn()
    }

    private fun updateDeviceTableColumn() {
        deviceTableModel.fireTableStructureChanged()
        // setup table column width from persisted status
        for (i in dialogConfig.col.indices) {
            val tableColumn = deviceTable.columnModel.getColumn(i)
            val colName = dialogConfig.col[i].name.toLowerCase()
            val width = dialogConfig.colWidth.getOrDefault(colName, 0)
            if (width != 0) {
                tableColumn.preferredWidth = width
            }
        }
        val buttonCol = dialogConfig.col.indexOf(COL.OPERATE)
        if (buttonCol < 0) return
        deviceTable.columnModel
                .getColumn(buttonCol).cellEditor = ButtonEditor(this)
        val column = deviceTable.columnModel.getColumn(buttonCol)
        column.cellRenderer = ButtonEditor(this)
    }

    private fun persistDialogStateAndDevicesInfo() {
        dialogConfig.height = contentPane.height
        dialogConfig.width = contentPane.width
        dialogConfig.x = x
        dialogConfig.y = y
        var markColIndex = -1
        try {
            for (i in dialogConfig.col.indices) {
                val column = deviceTable.columnModel.getColumn(i)
                val c = COL.valueOf(column.headerValue.toString().toUpperCase())
                if (c == COL.MARK) {
                    markColIndex = i
                }
                dialogConfig.colWidth[c.name.toLowerCase()] = column.width
                dialogConfig.col.remove(c)
                dialogConfig.col.add(c)
            }
            Config.saveDialogConfig(dialogConfig)
            if (markColIndex >= 0) {
                for (i in deviceList.indices) {
                    val mark = deviceTable.getValueAt(i, markColIndex).toString()
                    deviceList[i].mark = mark
                    println(deviceList[i])
                }
            }
            Config.saveDevice(deviceList)
        } catch (t: Throwable) {
            XLog.e(t)
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
                }
                item("Refresh Device") {
                    updateDeviceTable()
                }
                item("Connect Manual") {
                    ConnectDialog().show { updateDeviceTable() }
                }
                item("Exit") {
                    dispose()
                }
            }
            menu("ADB") {
                item("Restart Service") {
                    AdbUtils.restartServer()
                    updateDeviceTable()
                }
                item("Kill Service") {
                    AdbUtils.killServer()
                    updateDeviceTable()
                }
                item("Start Service") {
                    AdbUtils.startServer()
                    updateDeviceTable()
                }
            }
            menu("Settings") {
                item("Configure Adb Path") {
                    ConfigAdbDialog.createAndShow()
                }
                item("Custom Column") {
                    ConfigDialog.create {
                        initDeviceTable()
                        updateDeviceTable()
                        updateDeviceTableColumn()
                    }
                }
                item("Reset To Default") {
                    Config.saveDialogConfig(DialogConfig())
                    dialogConfig = Config.loadDialogConfig()
                    initDeviceTable()
                    updateDeviceTable()
                    updateDeviceTableColumn()
                }
            }
            menu("About") {
                item("About Tools") {
                    LogDialog.showAbout()
                }
            }
        }
    }
}