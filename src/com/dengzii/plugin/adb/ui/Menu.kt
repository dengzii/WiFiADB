package com.dengzii.plugin.adb.ui

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
class Menu(name: String?) {

    var menu: JMenu = JMenu(name)

    fun addItem(title: String?, onItemClick: OnItemClick) {
        val menuItem = JMenuItem(title)
        menuItem.addMouseListener(object : SimpleMouseListener() {
            override fun mousePressed(e: MouseEvent) {
                onItemClick.onItemClick()
            }
        })
        menu.add(menuItem)
    }

    interface OnItemClick {
        fun onItemClick()
    }
}