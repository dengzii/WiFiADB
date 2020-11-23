package com.dengzii.plugin.adb.tools.ui

import com.intellij.util.ui.AbstractTableCellEditor
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn
import javax.swing.table.TableColumnModel

class TableAdapter(private val columnInfo: List<ColumnInfo<Any>>) : AbstractTableModel() {

    private lateinit var jTable: JTable

    private val tableData: MutableList<MutableList<Any?>> = mutableListOf(
            mutableListOf("1", "2", "3"),
            mutableListOf("4", "5", "6"),
            mutableListOf("7", "8", "9")
    )

    fun setup(table: JTable) {
        jTable = table
        table.model = this
        table.columnModel = DelegateTableColumnModel(table.columnModel)
    }

    inner class DelegateTableColumnModel(columnModel: TableColumnModel) : TableColumnModelDecorator(columnModel) {

        private val defaultCellAdapter = DefaultCellAdapter()

        override fun getColumn(columnIndex: Int): TableColumn {
            val column = super.getColumn(columnIndex)
            column.cellRenderer = defaultCellAdapter
            column.cellEditor = defaultCellAdapter
            return column
        }
    }

    inner class DefaultCellAdapter
        : AbstractTableCellEditor(), TableCellRenderer {

        private lateinit var editorComponent: Component
        private lateinit var rendererComponent: Component

        override fun getCellEditorValue(): Any {
            return (editorComponent as? JTextField)?.text ?: "null"
        }

        override fun getTableCellEditorComponent(table: JTable?, value: Any?, isSelected: Boolean,
                                                 row: Int, column: Int): Component? {
            var e = columnInfo[column].getEditComponent(value)
            if (e == null) {
                e = if (value != null && table != null) {
                    table.getDefaultEditor(value::class.java)
                            .getTableCellEditorComponent(table, value, isSelected, row, column)
                } else {
                    JTextField(value.toString())
                }
            }
            editorComponent = e!!
            return editorComponent
        }

        override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean,
                                                   hasFocus: Boolean, row: Int, column: Int): Component? {
            var r = columnInfo[column].getRendererComponent(value)
            if (r == null) {
                r = if (value != null && table != null) {
                    table.getDefaultRenderer(value::class.java).getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column)
                } else {
                    JLabel(value?.toString().orEmpty())
                }
            }
            rendererComponent = r!!
            return rendererComponent
        }
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return columnInfo[columnIndex].columnClass
    }

    override fun getColumnName(column: Int): String {
        return columnInfo[column].name
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return columnInfo[columnIndex].isCellEditable(tableData[rowIndex][columnIndex])
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        tableData[rowIndex][columnIndex] = aValue
    }

    override fun getRowCount(): Int {
        return tableData.size
    }

    override fun getColumnCount(): Int {
        return columnInfo.size
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        return tableData[rowIndex][columnIndex].toString()
    }

}