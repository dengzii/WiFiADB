package com.dengzii.plugin.adb.ui

import com.dengzii.plugin.adb.tools.ui.XDialog
import com.intellij.ide.BrowserUtil
import com.intellij.ui.layout.applyToComponent
import com.intellij.ui.layout.panel
import com.intellij.util.ui.UIUtil


class AboutDialog : XDialog("About") {

    init {
        contentPane = panel {
            row {
                label("")
            }
            row {
                label("WiFiADB", style = UIUtil.ComponentStyle.LARGE).applyToComponent {
                    font = UIUtil.getLabelFont().deriveFont(24f)
                }.withLargeLeftGap()
            }
            row {
                label("Author: dengzi").withLargeLeftGap()
            }
            row {
                cell {
                    label("Rating:").withLargeLeftGap()
                    link("JetBrains Plugin Repository") {
                        browser("https://plugins.jetbrains.com/plugin/13156-android-wifiadb")
                    }
                }
            }
            row {
                cell {
                    label("Source Code:").withLargeLeftGap()
                    link("https://github.com/dengzii/WiFiADB") {
                        browser("https://github.com/dengzii/WiFiADB")
                    }
                }
            }
            row {
                cell {
                    label("If you have any questions or suggestions, please").apply {
                        withLargeLeftGap()
                    }.applyToComponent {
                        autoscrolls = true
                    }
                    link("Create Issue  ") {
                        browser("https://github.com/dengzii/WiFiADB/issues/new")
                    }
                }
            }
            row {
                label("")
            }
            row {
                label("")
            }
            row {
                label("Having a good day!",
                        fontColor = UIUtil.FontColor.BRIGHTER,
                        style = UIUtil.ComponentStyle.SMALL)
                        .withLargeLeftGap()
            }
        }
        persistDialogState = false
    }

    override fun pack() {
        super.pack()
        location = getLocationCenterOfScreen()
    }

    private fun browser(url: String) {
        BrowserUtil.browse(url)
    }
}

fun main() {
    AboutDialog().packAndShow()
}