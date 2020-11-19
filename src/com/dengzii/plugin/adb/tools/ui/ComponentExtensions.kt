package com.dengzii.plugin.adb.tools.ui

import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent


/**
 * Add right mouse button clicked listener to component.
 *
 * @param action The click event callback.
 */
inline fun Component.onRightMouseButtonClicked(crossinline action: (MouseEvent) -> Unit) {
    onMouseButtonClicked(MouseEvent.BUTTON3, action)
}


/**
 * Add mouse pressed listener.
 *
 * @param action The click event callback.
 */
inline fun Component.onClick(crossinline action: (MouseEvent?) -> Unit){
    addMouseListener(object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent?) {
            super.mousePressed(e)
            action(e)
        }
    })
}

/**
 * Add a listener to component when specified [button] clicked.
 *
 * @param button The mouse button code.
 * @param action The click event callback.
 */
inline fun Component.onMouseButtonClicked(button: Int, crossinline action: (MouseEvent) -> Unit) {
    addMouseListener(object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent?) {
            super.mouseClicked(e)
            if (e?.button == button) {
                action(e)
            }
        }
    })
}