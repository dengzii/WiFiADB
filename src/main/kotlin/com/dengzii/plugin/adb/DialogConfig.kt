package com.dengzii.plugin.adb

import com.google.gson.Gson

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/11/22
 * desc   :
 * </pre>
 */

class DialogConfig {

    var col = mutableListOf(
        ColumnEnum.SN,
        ColumnEnum.NAME,
        ColumnEnum.IP,
        ColumnEnum.PORT,
        ColumnEnum.STATUS,
        ColumnEnum.OPERATE
    )
    var colWidth = mutableMapOf(Pair("default", -1))

    companion object {
        const val ROW_HEIGHT = 30
        val INSTANCE by lazy { Config.loadDialogConfig() }

        fun fromSerialString(string: String): DialogConfig {
            return Gson().fromJson(string, DialogConfig::class.java) ?: DialogConfig()
        }
    }

    fun toSerialString(): String {
        return Gson().toJson(this)
    }

    enum class ColumnEnum {
        SN, MODEL_NAME, NAME, IP, PORT, STATUS, MARK, OPERATE;
    }
}