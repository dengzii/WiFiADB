package com.dengzii.plugin.adb.tools.ui

import com.intellij.util.ui.ColumnInfo
import java.awt.Component
import java.util.*
import javax.swing.JTable


class ColumnInfo<Item>(val name: String) {

    open val maxStringValue: String? get() = null
    open val preferredStringValue: String? get() = null
    open val additionalWidth: Int get() = 0
    open val comparator: Comparator<Item>? get() = null
    open val columnClass: Class<*> get() = String::class.java

//    abstract fun valueOf(var1: Item): Aspect?

    val isSortable: Boolean get() = comparator != null

    fun getEditorValue(component: Component): Item? {
        return null
    }

    fun isCellEditable(item: Item?): Boolean {
        return true
    }

    fun getRendererComponent(item: Item?): Component? {
        return null
    }

    fun getEditComponent(item: Item?): Component? {
        return null
    }

    open fun getWidth(table: JTable?): Int {
        return -1
    }

    fun hasError(): Boolean {
        return false
    }

    override fun equals(other: Any?): Boolean {
        return if (this === other) {
            true
        } else if (other != null && this.javaClass == other.javaClass) {
            val that = other as ColumnInfo<*, *>
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