package com.dengzii.plugin.adb.tools.ui

import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JMenu
import javax.swing.JMenuItem

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/11/23
 * desc   :
 * </pre>
 */
class XMenu(name: String?, private val block: XMenu.() -> Unit) : JMenu(name) {

    constructor(name: String?) : this(name, {})

    operator fun invoke(): XMenu {
        block(this)
        return this
    }

    fun addItem(title: String?, onItemClick: OnItemClick) {
        val menuItem = JMenuItem(title)
        menuItem.onClick {
            onItemClick.onItemClick()
        }
        add(menuItem)
    }

    fun item(title: String, onClick: () -> Unit) {
        addItem(title, object : OnItemClick {
            override fun onItemClick() {
                onClick()
            }
        })
    }

    fun onClick(onClick: () -> Unit){
        onMouseButtonClicked(MouseEvent.BUTTON1){
            onClick()
        }
    }

    interface OnItemClick {
        fun onItemClick()
    }
}