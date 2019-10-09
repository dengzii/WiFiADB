package com.dengzii.plugin.adb.ui

import com.dengzii.plugin.adb.Device
import com.sun.jna.StringArray
import javax.swing.table.AbstractTableModel

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/10/9
 * desc   :
 * </pre>
 */
class DeviceTableModel() : AbstractTableModel() {

    private val table: ArrayList<ArrayList<Any>> = arrayListOf()

    fun setData(devices: List<Device>){
        table.clear()
        devices.forEach {
            table.add(arrayListOf(it.sn, it.model, it.modelName, it.ip, it.port, it.status.name, it))
        }
    }

    override fun getRowCount(): Int {
        return table.size
    }

    override fun getColumnCount(): Int {
        return 7
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return columnIndex == 6
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        return table[rowIndex][columnIndex]
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return super.getColumnClass(columnIndex)
    }

    override fun getColumnName(column: Int): String {
        return super.getColumnName(column)
    }
}