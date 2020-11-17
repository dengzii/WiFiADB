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
class CmdResult(
        var exitCode: Int = 0,
        var output: String = "",
        var success: Boolean = false
)