package com.dengzii.plugin.adb.tools.ui

import javax.swing.JMenuBar

class XMenuBar(private val block: XMenuBar.() -> Unit) : JMenuBar() {

    init {
        block.invoke(this)
    }

    operator fun invoke(): XMenuBar {
        block.invoke(this)
        return this
    }

    fun menu(title: String, block: XMenu.() -> Unit) {
        val menu = XMenu(title)
        block(menu)
        add(menu)
    }
}