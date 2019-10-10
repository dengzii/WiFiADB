package com.dengzii.plugin.adb.ui

import com.dengzii.plugin.adb.Device
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


    companion object {
        private val TABLE_HEADER = arrayOf("Name", "Model_Name", "Model", "IP", "Port", "Status", "Operate")
    }

    fun setData(devices: List<Device>) {
        while (rowCount > 0) {
            removeRow(0)
        }
        fireTableDataChanged()
        devices.forEach {
            addRow(arrayOf(it.sn, it.model, it.modelName, it.ip, it.port, it.status.name, it))
        }
        fireTableDataChanged()
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return if (columnIndex == columnCount) Device::class.java else String::class.java
    }

    override fun getColumnCount(): Int {
        return 7
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return columnIndex == 6
    }

    override fun getColumnName(column: Int): String {
        return TABLE_HEADER[column]
    }
}