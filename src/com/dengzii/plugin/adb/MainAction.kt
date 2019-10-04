package com.dengzii.plugin.adb

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

import java.io.IOException

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/10/4
 * desc   :
</pre> *
 */
class MainAction : AnAction() {

    override fun actionPerformed(anActionEvent: AnActionEvent) {

        try {
            val process = Runtime.getRuntime().exec("adb devices")

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
