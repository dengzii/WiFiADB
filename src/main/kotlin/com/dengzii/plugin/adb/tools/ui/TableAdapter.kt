package com.dengzii.plugin.adb.tools.ui

import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.AbstractTableCellEditor
import java.awt.Component
import javax.swing.JTable
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn
import javax.swing.table.TableColumnModel

/**
 * ## The JTable Adapter
 *
 * This is an adapter use for auto manager JTable data model.
 *
 * @param tableData The table data of each column and each row, you should ensure each row has the same size.
 * @param columnInfo The column model express how to display and edit.
 *
 * @author https://github.com/dengzii
 */
open class TableAdapter(private val tableData: MutableList<MutableList<Any?>>,
                        private val columnInfo: MutableList<ColumnInfo<Any>>) : AbstractTableModel() {

    private lateinit var jTable: JTable

    // the map of table column header value and ColumnInfo
    // using for find ColumnInfo when don't know column index.
    private var columnInfoMap = mutableMapOf<Any, ColumnInfo<Any>>()

    // the table cell adapter
    var cellAdapter: CellAdapter = DefaultCellAdapter()

    fun setup(table: JTable) {
        jTable = table
        table.model = this
        table.columnModel = DelegateTableColumnModel(table.columnModel)
    }

    fun eachColumn(action: (TableColumn, Int) -> Unit) {
        for (i in 0 until jTable.columnModel.columnCount) {
            action(jTable.columnModel.getColumn(i), i)
        }
    }

    /**
     * Wrapper of JTable's default column model.
     *
     * Using for custom column renderer and editor.
     */
    inner class DelegateTableColumnModel(columnModel: TableColumnModel) : TableColumnModelDecorator(columnModel) {

        override fun getColumn(columnIndex: Int): TableColumn {
            val column = super.getColumn(columnIndex)
            column.cellRenderer = cellAdapter
            column.cellEditor = cellAdapter
            columnInfoMap[column.headerValue]?.columnWidth?.takeIf {
                it > 0
            }?.let {
                column.preferredWidth = it
            }
            return column
        }
    }

    abstract class CellAdapter : AbstractTableCellEditor(), TableCellRenderer

    /**
     * Definition how cell render and edit.
     */
    inner class DefaultCellAdapter : CellAdapter() {

        private lateinit var editorComponent: Component
        private lateinit var rendererComponent: Component
        private var value: Any? = null
        private var editLocation = Pair(-1, -1)

        override fun getCellEditorValue(): Any? {
            val v = columnInfo[editLocation.second].getEditorValue(
                    editorComponent, value, editLocation.first, editLocation.second)
            return v ?: (editorComponent as? JBTextField)?.text ?: value
        }

        override fun getTableCellEditorComponent(table: JTable?, value: Any?, isSelected: Boolean,
                                                 row: Int, column: Int): Component? {
            // temp the old value before edit cell
            this.value = value
            editLocation = Pair(row, column)
            val header = jTable.columnModel.getColumn(column).headerValue
            // get the edit component from ColumnInfo
            editorComponent = columnInfoMap[header]?.getEditComponent(value, row, column) ?: return null
            return editorComponent
        }

        override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean,
                                                   hasFocus: Boolean, row: Int, column: Int): Component? {
            val header = jTable.columnModel.getColumn(column).headerValue
            rendererComponent = columnInfoMap[header]?.getRendererComponent(value, row, column) ?: return null
            return rendererComponent
        }
    }

    override fun fireTableStructureChanged() {
        // when table structure changed, the columns may changed.
        columnInfoMap.clear()
        columnInfo.forEach {
            columnInfoMap[it.colName] = it
        }
        super.fireTableStructureChanged()
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return columnInfo[columnIndex].columnClass
    }

    override fun getColumnName(column: Int): String {
        return columnInfo[column].colName
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return columnInfo[columnIndex].isCellEditable(tableData[rowIndex].getOrNull(columnIndex))
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
        return tableData[rowIndex].getOrNull(columnIndex)
    }

}