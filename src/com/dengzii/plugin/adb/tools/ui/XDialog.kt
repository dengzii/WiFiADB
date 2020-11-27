package com.dengzii.plugin.adb.tools.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.WindowStateService
import java.awt.Point
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.annotation.OverridingMethodsMustInvokeSuper
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.JPanel
import javax.swing.KeyStroke

/**
 * The class implements some frequently-used feature of [JDialog].
 *
 * - Add dialog lifecycle callback, like opened, closed etc.
 * - Default be modal.
 * - Default dispose on dialog close.
 * - Persist the size, location, and restore it the next time opened.
 *
 * @author https://github.com/dengzii
 */
abstract class XDialog() : JDialog() {

    var persistDialogState = true
    var project: Project? = null

    private val keyWindowStatePersist = this::class.java.name

    constructor(title: String) : this() {
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
                    hideAndDispose()
                }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                super.windowOpened(e)
            }

            override fun windowClosing(e: WindowEvent) {
                onClosing()
            }

            override fun windowClosed(e: WindowEvent?) {
                super.windowClosed(e)
                onClosed()
            }
        })
    }

    fun getLocationCenterOfScreen(): Point {
        val screen = toolkit.screenSize
        val x = screen.width / 2 - width / 2
        val y = screen.height / 2 - height /2
        return Point(x, y)
    }

    fun packAndShow() {
        pack()
        isVisible = true
    }

    fun hideAndDispose() {
        isVisible = false
        dispose()
    }

    @OverridingMethodsMustInvokeSuper
    open fun onOpened() {
        if (persistDialogState) {
            restoreState()
        }
    }

    @OverridingMethodsMustInvokeSuper
    open fun onClosing() {
        if (persistDialogState) {
            persistState()
        }
    }

    @OverridingMethodsMustInvokeSuper
    open fun onClosed() {

    }

    /**
     * Doing some work about persistent.
     */
    open fun persistState() {
        WindowStateService.getInstance().putBounds(keyWindowStatePersist, Rectangle(x, y, width, height))
    }

    /**
     * Restore the state of dialog.
     */
    open fun restoreState() {
        val bounds = WindowStateService.getInstance().getBounds(keyWindowStatePersist)
                ?: Rectangle().apply {
                    val screenSize = Toolkit.getDefaultToolkit().screenSize
                    height = 300
                    width = 500
                    y = screenSize.height / 2 - height
                    x = screenSize.width / 2 - width / 2
                }
        setBounds(bounds)
    }
}