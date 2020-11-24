package com.dengzii.plugin.adb.tools.ui

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import java.awt.Component


/**
 * The model of the column of [JTable].
 *
 * @author https://github.com/dengzii
 */
open class ColumnInfo<Item>(val colName: String) {

    private var editable: Boolean = false

    open val columnClass: Class<*> get() = String::class.java

    open var columnWidth: Int? = null

    constructor(colName: String, editable: Boolean) : this(colName) {
        this.editable = editable
    }

    companion object {
        fun of(vararg columns: String): List<ColumnInfo<Any>> {
            return columns.map { ColumnInfo(it) }
        }
    }

    /**
     * Return the value of column when edit finish, if this column is editable.
     *
     * @param component The edit component.
     * @param oldValue The old value before edit.
     * @return A new value.
     */
    open fun getEditorValue(component: Component, oldValue: Item?, row: Int, col: Int): Item? {
        return null
    }

    /**
     * Whether this column editable.
     * @param item The item value.
     * @return True is editable, otherwise not.
     */
    open fun isCellEditable(item: Item?): Boolean {
        return editable
    }

    /**
     * Return a [Component] use for display item value.
     * @param item The item value.
     * @return The component.
     */
    open fun getRendererComponent(item: Item?, row: Int, col: Int): Component {
        return JBLabel(item?.toString().orEmpty())
    }

    /**
     * Return a [Component] use for edit this column.
     *
     * Working just when [isCellEditable] returns true.
     *
     * @param item The item value.
     * @return The component.
     */
    open fun getEditComponent(item: Item?, row: Int, col: Int): Component {
        return JBTextField(item?.toString().orEmpty())
    }

    override fun equals(other: Any?): Boolean {
        return if (this === other) {
            true
        } else if (other != null && this.javaClass == other.javaClass) {
            val that = other as ColumnInfo<*>
            colName == that.colName
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return colName.hashCode()
    }

    override fun toString(): String {
        return colName
    }
}