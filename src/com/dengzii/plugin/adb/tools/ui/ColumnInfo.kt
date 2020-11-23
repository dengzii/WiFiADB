package com.dengzii.plugin.adb.tools.ui

import java.awt.Component
import java.util.*
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.JTextField


open class ColumnInfo<Item>(val name: String) {

    open val maxStringValue: String? get() = null
    open val preferredStringValue: String? get() = null
    open val additionalWidth: Int get() = 0
    open val comparator: Comparator<Item>? get() = null
    open val columnClass: Class<*> get() = String::class.java

    companion object {
        fun of(vararg columns: String): List<ColumnInfo<Any>> {
            return columns.map { ColumnInfo<Any>(it) }
        }
    }

    open val isSortable: Boolean get() = comparator != null

    open fun getEditorValue(component: Component, oldValue: Item?): Item? {
        return oldValue
    }

    open fun isCellEditable(item: Item?): Boolean {
        return false
    }

    open fun getRendererComponent(item: Item?): Component? {
        return JLabel(item.toString())
    }

    open fun getEditComponent(item: Item?): Component? {
        return JTextField(item.toString())
    }

    open fun getWidth(table: JTable?): Int {
        return -1
    }

    open fun hasError(): Boolean {
        return false
    }

    override fun equals(other: Any?): Boolean {
        return if (this === other) {
            true
        } else if (other != null && this.javaClass == other.javaClass) {
            val that = other as ColumnInfo<*>
            name == that.name
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return name
    }
}