package com.dengzii.plugin.adb

import com.dengzii.plugin.adb.tools.NotificationUtils
import com.google.gson.Gson
import java.awt.Toolkit

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/11/22
 * desc   :
 * </pre>
 */

class DialogConfig() {

    var width: Int = 550
    var height: Int = 300
    var x: Int = 0
    var y: Int = 0

    var col = mutableListOf(COL.SN, COL.NAME, COL.IP, COL.PORT, COL.STATUS, COL.OPERATE)
    var colWidth = mutableMapOf(Pair("default", 0))

    init {
        NotificationUtils.showInfo("init")
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        width = screenSize.width / 2 - width / 2
        height = screenSize.height / 2 - height
    }

    companion object {
        const val ROW_HEIGHT = 30
        val INSTANCE by lazy { Config.loadDialogConfig() }

        fun fromSerialString(string: String): DialogConfig {
            return Gson().fromJson(string, DialogConfig::class.java)?: DialogConfig()
        }
    }

    fun toSerialString(): String {
        return Gson().toJson(this)
    }

    override fun toString(): String {
        return "DialogConfig(width=$width, height=$height, x=$x, y=$y, col=$col)"
    }

    enum class COL {
        SN, MODEL_NAME, NAME, IP, PORT, STATUS, MARK, OPERATE;

        companion object {
            fun reverseValues(): Array<COL> {
                val r = values()
                r.reverse()
                return r
            }
        }

    }
}