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

    var width: Int = 0
    var height: Int = 300
    var x: Int = 0
    var y: Int = 0

    var col = mutableListOf(COL.SN, COL.NAME, COL.IP, COL.PORT, COL.STATUS, COL.OPERATE)
    var colWidth = mutableMapOf(Pair("default", 0))

    companion object {
        const val ROW_HEIGHT = 30
        val INSTANCE by lazy { Config.loadDialogConfig() }

        fun fromSerialString(string: String): DialogConfig {
            if (string.trim().isEmpty()) {
                return DialogConfig()
            }
            val bounds = string.split("##").getOrNull(0) ?: ""
            val col = string.split("##").getOrNull(1) ?: ""
            val colWidth = string.split("##").getOrNull(2) ?: ""
            return DialogConfig(col, bounds, colWidth)
        }
    }

    private constructor(colStr: String, boundsStr: String, colWidthStr: String) : this() {

        if (colStr.isNotBlank()) {
            col.clear()
            colStr.split("#").forEach {
                col.add(COL.valueOf(it))
            }
        }

        if (colWidthStr.isNotBlank()) {
            colWidthStr.split("#").forEach {
                val pair = it.split("=")
                colWidth[pair[0]] = pair[1].toInt()
            }
        }
        if (boundsStr.isNotBlank()) {
            val b = boundsStr.split("#").filter { !it.isBlank() }

            width = b[0].toInt()
            height = b[1].toInt()
            x = b[2].toInt()
            y = b[3].toInt()
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
        s.append("#")
        colWidth.forEach { (k, v) ->
            s.append("#$k=$v")
        }
        return s.toString()
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