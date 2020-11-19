package com.dengzii.plugin.adb.tools

import com.intellij.notification.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.util.Alarm

/**
 * <pre>
 * author : dengzi
 * e-mail : dengzii@foxmail.com
 * github : https://github.com/dengzii
 * time   : 2020/11/18
 * desc   : Utils about Notification.
</pre> *
 */
object NotificationUtils {

    var defaultTitle = "Notification"

    var balloonGroup = NotificationGroup("Untitled_Notification", NotificationDisplayType.BALLOON, true)
    var stickyGroup = NotificationGroup("Untitled_Notification", NotificationDisplayType.STICKY_BALLOON, true)

    fun getInfo(msg: String, title: String = defaultTitle): Notification {
        return balloonGroup.createNotification(NotificationType.INFORMATION).apply {
            setTitle(title)
            setContent(msg)
        }
    }

    fun getError(msg: String, title: String = defaultTitle): Notification {
        return stickyGroup.createNotification(NotificationType.ERROR).apply {
            setTitle(title)
            setContent(msg)
        }
    }

    fun getWarning(msg: String, title: String = defaultTitle): Notification {
        return balloonGroup.createNotification(NotificationType.WARNING).apply {
            setTitle(title)
            setContent(msg)
        }
    }

    fun showInfo(msg: String, title: String = defaultTitle): Notification {
        return getInfo(msg, title).show()
    }

    fun showError(msg: String, title: String = defaultTitle): Notification {
        return getError(msg, title).show()
    }

    fun showWarning(msg: String, title: String = defaultTitle): Notification {
        return getWarning(msg, title).show()
    }

    fun Notification.show(expireMillis: Long? = null, project: Project? = null): Notification {
        Notifications.Bus.notify(this, project)
        if (expireMillis != null) {
            val alarm = Alarm(((project ?: ApplicationManager.getApplication()) as Disposable))
            alarm.addRequest({
                expire()
                Disposer.dispose(alarm)
            }, expireMillis)
        }
        return this
    }
}
