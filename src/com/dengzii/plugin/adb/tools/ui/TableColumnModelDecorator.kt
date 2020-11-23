package com.dengzii.plugin.adb.tools.ui

import java.util.*
import javax.swing.ListSelectionModel
import javax.swing.event.TableColumnModelListener
import javax.swing.table.TableColumn
import javax.swing.table.TableColumnModel

open class TableColumnModelDecorator(private val wrapper: TableColumnModel) : TableColumnModel {

    override fun addColumn(aColumn: TableColumn?) {
        wrapper.addColumn(aColumn)
    }

    override fun removeColumn(column: TableColumn?) {
        wrapper.removeColumn(column)
    }

    override fun moveColumn(columnIndex: Int, newIndex: Int) {
        wrapper.moveColumn(columnIndex, newIndex)
    }

    override fun setColumnMargin(newMargin: Int) {
        wrapper.columnMargin = newMargin
    }

    override fun getColumnCount(): Int {
        return wrapper.columnCount
    }

    override fun getColumns(): Enumeration<TableColumn> {
        return wrapper.columns
    }

    override fun getColumnIndex(columnIdentifier: Any?): Int {
        return wrapper.getColumnIndex(columnIdentifier)
    }

    override fun getColumn(columnIndex: Int): TableColumn {
        return wrapper.getColumn(columnIndex)
    }

    override fun getColumnMargin(): Int {
        return wrapper.columnMargin
    }

    override fun getColumnIndexAtX(xPosition: Int): Int {
        return wrapper.getColumnIndexAtX(xPosition)
    }

    override fun getTotalColumnWidth(): Int {
        return wrapper.totalColumnWidth
    }

    override fun setColumnSelectionAllowed(flag: Boolean) {
        wrapper.columnSelectionAllowed = flag
    }

    override fun getColumnSelectionAllowed(): Boolean {
        return wrapper.columnSelectionAllowed
    }

    override fun getSelectedColumns(): IntArray {
        return wrapper.selectedColumns
    }

    override fun getSelectedColumnCount(): Int {
        return wrapper.selectedColumnCount
    }

    override fun setSelectionModel(newModel: ListSelectionModel?) {
        wrapper.selectionModel = newModel
    }

    override fun getSelectionModel(): ListSelectionModel {
        return wrapper.selectionModel
    }

    override fun addColumnModelListener(x: TableColumnModelListener?) {
        wrapper.addColumnModelListener(x)
    }

    override fun removeColumnModelListener(x: TableColumnModelListener?) {
        wrapper.removeColumnModelListener(x)
    }
}