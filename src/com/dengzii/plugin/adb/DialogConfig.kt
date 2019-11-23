package com.dengzii.plugin.adb

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/11/22
 * desc   :
 * </pre>
 */

class DialogConfig() {

    private var width: Int = 0
    private var height: Int = 300
    private var x: Int = 0
    private var y: Int = 0

    private var col = mutableListOf(COL.SN, COL.NAME, COL.IP, COL.PORT, COL.STATUS)

    private constructor(colStr: String, boundsStr: String) : this() {

        colStr.split("#").forEach {
            col.add(COL.valueOf(it))
        }
        val b = boundsStr.split("#").filter { !it.isBlank() }

        width = b[0].toInt()
        height = b[1].toInt()
        x = b[2].toInt()
        y = b[3].toInt()
    }

    companion object {

        fun fromSerialString(string: String): DialogConfig {
            if (string.trim().isEmpty()) {
                return DialogConfig()
            }
            val bounds = string.split("##")[0]
            val col = string.split("##")[1]
            return DialogConfig(col, bounds)
        }
    }

    fun toSerialString(): String {
        val s = StringBuilder()
        val ls = listOf(width, height, x, y)
        ls.forEach {
            s.append("#$it")
        }
        s.append("#")
        col.forEach {
            s.append("#${it.name}")
        }
        return s.toString()
    }

    enum class COL {
        SN, MODEL_NAME, NAME, IP, PORT, STATUS, MARK
    }
}