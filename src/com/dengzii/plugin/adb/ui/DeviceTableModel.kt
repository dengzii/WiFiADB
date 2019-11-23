package com.dengzii.plugin.adb.ui

import com.dengzii.plugin.adb.Device
import com.dengzii.plugin.adb.DialogConfig
import java.util.*
import javax.swing.table.DefaultTableModel

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/10/9
 * desc   :
 * </pre>
 */
class DeviceTableModel : DefaultTableModel() {

    private val dialogConfig = DialogConfig.INSTANCE
    private val tableHeader = dialogConfig.col
    private val editableCol = arrayOf(
            dialogConfig.col.indexOf(DialogConfig.COL.OPERATE),
            dialogConfig.col.indexOf(DialogConfig.COL.MARK)
    )

    fun setData(devices: List<Device>) {
        while (rowCount > 0) {
            removeRow(0)
        }
        fireTableDataChanged()
        devices.forEach { device ->
            val rowList = ArrayList<Any>()
            dialogConfig.col.forEach { c ->
                rowList.add(when (c) {
                    DialogConfig.COL.SN -> device.sn
                    DialogConfig.COL.MODEL_NAME -> device.modelName
                    DialogConfig.COL.NAME -> device.model
                    DialogConfig.COL.IP -> device.ip
                    DialogConfig.COL.PORT -> device.port
                    DialogConfig.COL.STATUS -> device.status
                    DialogConfig.COL.MARK -> device.mark
                    DialogConfig.COL.OPERATE -> device
                })
            }
            addRow(rowList.toArray())
        }
        fireTableDataChanged()
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return if (columnIndex == columnCount) Device::class.java else String::class.java
    }

    override fun getColumnCount(): Int {
        return dialogConfig.col.size
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return columnIndex in editableCol
    }

    override fun getColumnName(column: Int): String {
        return tableHeader[column].name.toLowerCase()
    }
}