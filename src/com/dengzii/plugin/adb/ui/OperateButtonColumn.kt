package com.dengzii.plugin.adb.ui

import com.dengzii.plugin.adb.Device
import com.dengzii.plugin.adb.XLog
import com.dengzii.plugin.adb.tools.ui.ColumnInfo
import com.dengzii.plugin.adb.tools.ui.onClick
import com.intellij.ui.components.JBLabel
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import javax.swing.JButton
import javax.swing.JPanel

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/10/9
 * desc   :
 * </pre>
 */
class OperateButtonColumn(colName: String, private val clickListener: (device: Device) -> Unit) : ColumnInfo<Any>(colName) {

    override val columnClass = Device::class.java

    override fun isCellEditable(item: Any?): Boolean {
        return true
    }

    override fun getEditorValue(component: Component, oldValue: Any?, row: Int, col: Int): Any? {
        return oldValue
    }

    override fun getEditComponent(item: Any?, row: Int, col: Int): Component {
        return getButton(item)
    }

    override fun getRendererComponent(item: Any?, row: Int, col: Int): Component {
        return getButton(item)
    }

    private fun getButton(device: Any?): Component {

        if (device !is Device) {
            XLog.e(device.toString())
            return JBLabel("-")
        }
        val status = device.status
        val panel = JPanel()
        panel.layout = BorderLayout()

        val button = Button(status.name.toLowerCase())
        button.isEnabled = true
        when (status) {
            Device.Status.CONNECTED -> {
                button.text = "disconnect"
                button.redText()
            }
            Device.Status.USB,
            Device.Status.DISCONNECTED -> {
                button.text = "connect"
                button.greenText()
            }
            Device.Status.OFFLINE->{
                button.text = "offline"
                button.isEnabled = false
            }
            else -> {
                button.isEnabled = false
            }
        }
        button.onClick {
            clickListener.invoke(device)
        }
        panel.add(button, BorderLayout.CENTER)
        return panel
    }

    class Button(text: String = "") : JButton(text) {

        init {
            font = font.deriveFont(font.size - 2f)
            isBorderPainted = false
            isFocusPainted = false
        }

        private fun setColor(color: Color) {
            foreground = color
        }

        fun greenText() {
            val hsb = FloatArray(3)
            Color.RGBtoHSB(62, 143, 94, hsb)
            setColor(Color.getHSBColor(hsb[0], hsb[1], hsb[2]))
        }

        fun redText() {
            val hsb = FloatArray(3)
            Color.RGBtoHSB(199, 23, 23, hsb)
            setColor(Color.getHSBColor(hsb[0], hsb[1], hsb[2]))
        }
    }
}