package com.dengzii.plugin.adb.ui

import com.dengzii.plugin.adb.Device
import com.dengzii.plugin.adb.XLog
import com.dengzii.plugin.adb.utils.AdbUtils
import com.intellij.ui.components.JBLabel
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.AbstractCellEditor
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.SwingUtilities
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/10/9
 * desc   :
 * </pre>
 */
class ButtonEditor(private val dialog: AdbDialog) : AbstractCellEditor(), TableCellEditor, TableCellRenderer {

    private lateinit var value: Device

    override fun getTableCellRendererComponent(table: JTable?, value: Any?,
                                               isSelected: Boolean, hasFocus: Boolean,
                                               row: Int, column: Int): Component {
        return getButton(value)
    }

    override fun getTableCellEditorComponent(table: JTable?, device: Any?, isSelected: Boolean,
                                             row: Int, column: Int): Component {
        value = device as Device
        return getButton(device)
    }

    override fun getCellEditorValue(): Any {
        return value
    }

    private fun getButton(device: Any?): Component {

        if (device !is Device) {
            XLog.d("ButtonEditor.getButton", device.toString())
            return JBLabel("-")
        }
        val status = device.status
        val panel = JPanel()
        panel.layout = BorderLayout()

        val button = Button(status.name.toLowerCase())
        button.isEnabled = false
        when (status) {
            Device.Status.CONNECTED -> {
                button.isEnabled = true
                button.text = "disconnect"
                button.redText()
            }
            Device.Status.DISCONNECTED -> {
                button.isEnabled = true
                button.text = "connect"
                button.greenText()
            }
            Device.Status.ONLINE, Device.Status.DISCONNECT -> {
                if (!AdbUtils.isIpConnected(device.ip)) {
                    button.isEnabled = true
                    button.text = "connect"
                    button.greenText()
                } else {
                    button.text = "local"
                }
            }
        }
        button.addActionListener {
            if (button.isEnabled) {
                clicked(device)
            }
        }

        panel.add(button, BorderLayout.CENTER)
        return panel
    }

    private fun clicked(device: Device) {

        when (device.status) {
            Device.Status.CONNECTED -> {
                disconnect(device)
            }
            Device.Status.ONLINE, Device.Status.DISCONNECT, Device.Status.DISCONNECTED -> {
                connect(device)
            }
        }
    }

    private fun connect(device: Device) {
        device.connect()
        Thread {
            Thread.sleep(500)
            SwingUtilities.invokeLater { dialog.update() }
        }.start()
    }

    private fun disconnect(device: Device) {
        device.disconnect()
        Thread {
            Thread.sleep(500)
            SwingUtilities.invokeLater { dialog.update() }
        }.start()
    }
}