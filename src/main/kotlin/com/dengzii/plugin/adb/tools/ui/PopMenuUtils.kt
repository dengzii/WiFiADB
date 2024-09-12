package com.dengzii.plugin.adb.tools.ui

import com.intellij.openapi.ui.JBMenuItem
import com.intellij.openapi.ui.JBPopupMenu
import java.awt.Component
import java.awt.event.MouseEvent

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2020/1/16
 * desc   :
 * </pre>
 */
object PopMenuUtils {

    /**
     * Return a [JBPopupMenu] instance with [menu].
     *
     * @param menu The menu item and click event callback pair.
     */
    fun create(menu: Map<String, () -> Unit?>): JBPopupMenu {

        val popMenu = JBPopupMenu()
        menu.forEach { (k, v) ->
            if (k.isBlank()) {
                popMenu.addSeparator()
                return@forEach
            }
            val item = JBMenuItem(k)
            item.addActionListener {
                v.invoke()
            }
            popMenu.add(item)
        }
        return popMenu
    }

    /**
     * Show a [JBPopupMenu] that location depends on [event]'s x, y.
     *
     * @param event The mouse event.
     * @param menu The menu item and click event callback pair.
     */
    fun show(event: MouseEvent, menu: Map<String, () -> Unit?>) {
        create(menu).show(event.source as Component, event.x, event.y)
    }
}