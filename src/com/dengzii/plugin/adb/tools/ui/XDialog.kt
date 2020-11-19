package com.dengzii.plugin.adb.tools.ui

import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.JPanel
import javax.swing.KeyStroke

abstract class XDialog() : JDialog() {

    constructor(title: String) : this(){
        this.title = title
    }

    init {
        isModal = true
        defaultCloseOperation = DISPOSE_ON_CLOSE
        addWindowListener(object : WindowAdapter() {

            override fun windowOpened(e: WindowEvent?) {
                onOpened()
                (contentPane as? JPanel)?.registerKeyboardAction({
                    onClosing()
                    dispose()
                }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)

                super.windowOpened(e)
            }

            override fun windowClosing(e: WindowEvent) {
                onClosing()
            }

            override fun windowClosed(e: WindowEvent?) {
                super.windowClosed(e)
                onClosed()
                dispose()
            }
        })
    }

    fun setCenterOfScreen() {
        val screen = toolkit.screenSize
        val x = screen.width / 2 - width / 2
        val y = screen.height / 2 - height
        setLocation(x, y)
    }

    fun packAndShow() {
        pack()
        isVisible = true
    }

    open fun onOpened() {

    }

    open fun onClosing() {

    }

    open fun onClosed() {

    }
}