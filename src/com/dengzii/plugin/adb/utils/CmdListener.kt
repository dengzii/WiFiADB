package com.dengzii.plugin.adb.utils

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/10/8
 * desc   :
 * </pre>
 */
interface CmdListener {
    fun onExecuted(success: Boolean, code: Int, msg: String)
}