package com.dengzii.plugin.adb.ui

import java.awt.event.MouseEvent
import java.awt.event.MouseListener

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/11/23
 * desc   :
 * </pre>
 */
abstract class SimpleMouseListener : MouseListener {
    override fun mouseClicked(e: MouseEvent?) {}
    override fun mouseReleased(e: MouseEvent?) {}
    override fun mouseEntered(e: MouseEvent?) {}
    override fun mouseExited(e: MouseEvent?) {}
}