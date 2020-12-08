package com.dengzii.plugin.adb.ui

import com.dengzii.plugin.adb.tools.ui.XDialog
import com.intellij.ide.BrowserUtil
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.ui.layout.applyToComponent
import com.intellij.ui.layout.panel
import com.intellij.util.ui.UIUtil
import java.awt.Dimension
import java.awt.Font

class AboutDialog : XDialog("About") {

    private val version by lazy {
        PluginManager.getPlugins().first {
            it.pluginId == PluginId.getId("com.dengzii.plugin.adb")
        }.version
    }

    init {
        persistDialogState = false
        contentPane = panel {
            row {
                label("")
            }
            row {
                label("WiFiADB", style = UIUtil.ComponentStyle.LARGE, bold = true).applyToComponent {
                    font = UIUtil.getLabelFont().deriveFont(24f).deriveFont(Font.BOLD)
                }.withLargeLeftGap()
            }
            row {
                cell {
                    label("Version: ").withLargeLeftGap()
                    label(version)
                }
            }
            row {
                cell {
                    label("Reference:").withLargeLeftGap()
                    link("Android Debug Bridge") {
                        browser("https://developer.android.com/studio/command-line/adb")
                    }
                }
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
    }

    override fun onOpened() {
        super.onOpened()
        size = Dimension(400, 300)
        location = getLocationCenterOfScreen()
    }

    private fun browser(url: String) {
        BrowserUtil.browse(url)
    }
}