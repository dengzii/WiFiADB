package com.dengzii.plugin.adb.utils

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/10/8
 * desc   :
 * </pre>
 */
interface CmdListener {
    fun onExecuted(success: Boolean, code: Int, msg: String)
}