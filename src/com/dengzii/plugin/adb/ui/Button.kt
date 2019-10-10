package com.dengzii.plugin.adb.ui

import java.awt.Color
import javax.swing.JButton

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/10/9
 * desc   :
 * </pre>
 */
class Button(text: String = "") : JButton(text) {

    init {
        font = font.deriveFont(font.size - 2f)
        isBorderPainted = false
        isFocusPainted = false
    }

    fun setColor(color: Color) {
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