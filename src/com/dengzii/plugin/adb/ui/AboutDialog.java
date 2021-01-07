package com.dengzii.plugin.adb.ui;

import com.dengzii.plugin.adb.tools.ui.XDialog;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.ui.components.labels.LinkLabel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class AboutDialog extends XDialog {
    private LinkLabel<String> labelAdb;
    private LinkLabel<String> labelRating;
    private LinkLabel<String> labelCode;
    private LinkLabel<String> labelIssue;
    private JLabel labelVersion;
    private JPanel content;

    public static void show_(){
        Dialog dialog = new AboutDialog();
        dialog.pack();
        dialog.setVisible(true);
    }

    AboutDialog() {
        super("About");
        setContentPane(content);

        try {
            labelVersion.setText(Arrays.stream(PluginManager.getPlugins()).filter(ideaPluginDescriptor -> {
                return ideaPluginDescriptor.getPluginId() == PluginId.getId("com.dengzii.plugin.adb");
            }).findFirst().get().getVersion());
        } catch (Exception ignored) {

        }

        labelAdb.setListener((linkLabel, o) -> {
            BrowserUtil.browse(o);
        }, "https://developer.android.com/studio/command-line/adb");

        labelRating.setListener((linkLabel, o) -> {
            BrowserUtil.browse(o);
        }, "https://plugins.jetbrains.com/plugin/13156-android-wifiadb");

        labelCode.setListener((linkLabel, o) -> {
            BrowserUtil.browse(o);
        }, "https://github.com/dengzii/WiFiADB");

        labelIssue.setListener((linkLabel, o) -> {
            BrowserUtil.browse(o);
        }, "https://github.com/dengzii/WiFiADB/issues");
    }

    @Override
    public void pack() {
        super.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = getWidth();
        int h = getHeight();
        int x = 400 - w / 2;
        int y = screen.height / 2 - h;
        setLocation(x, y);
        setPreferredSize(new Dimension(w, h));
    }
}
