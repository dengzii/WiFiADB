package com.dengzii.plugin.adb

import com.dengzii.plugin.adb.tools.NotificationUtils
import com.dengzii.plugin.adb.ui.MainDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2019/10/4
 * desc   :
</pre> *
 */
class MainAction : AnAction() {

    override fun actionPerformed(anActionEvent: AnActionEvent) {

//        val adb = AndroidSdkUtils.getDebugBridge(anActionEvent.project!!)
//        adb?.devices?.forEach {
//            println(it.name)
//        }
        try {
            val dialog = MainDialog()
            dialog.show()
        } catch (e: Throwable) {
            XLog.e(e)
            NotificationUtils.showError("Unfortunately, something has gone wrong: ${e.localizedMessage}", "WiFiADB")
        }
    }
}
