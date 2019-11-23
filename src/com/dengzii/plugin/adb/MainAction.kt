package com.dengzii.plugin.adb

import com.dengzii.plugin.adb.ui.AdbDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

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

        val dialog = AdbDialog()
        dialog.updateTable()
        dialog.pack()
        dialog.isVisible = true
    }
}
