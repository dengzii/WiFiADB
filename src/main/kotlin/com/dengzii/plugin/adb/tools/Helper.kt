package com.dengzii.plugin.adb.tools

import com.intellij.openapi.application.ApplicationManager

fun invokeLater(action: () -> Unit) {
    ApplicationManager.getApplication().invokeLater(action)
}