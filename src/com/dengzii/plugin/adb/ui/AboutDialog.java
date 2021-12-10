package com.dengzii.plugin.adb.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class AboutDialog extends JDialog {
    private JLabel labelVersion;
    private JPanel content;
    private JLabel reference;
    private JLabel rating;
    private JLabel sourceCode;
    private JLabel issue;
    private JLabel labelFree;

    public static void show_() {
        Dialog dialog = new AboutDialog();
        dialog.pack();
        dialog.setVisible(true);
    }

    AboutDialog() {
        setContentPane(content);
        setModal(true);

        try {
            labelVersion.setText(Arrays.stream(PluginManager.getPlugins()).filter(ideaPluginDescriptor -> {
                return ideaPluginDescriptor.getPluginId() == PluginId.getId("com.dengzii.plugin.adb");
            }).findFirst().get().getVersion());
        } catch (Throwable ignored) {

        }

        setUrlEvent(reference, "https://developer.android.com/studio/command-line/adb");
        setUrlEvent(rating, "https://plugins.jetbrains.com/plugin/13156-android-wifiadb");
        setUrlEvent(sourceCode, "https://github.com/dengzii/WiFiADB");
        setUrlEvent(issue, "https://github.com/dengzii/WiFiADB/issues");
        setUrlEvent(labelFree, "https://github.com/dengzii/WiFiADB/releases");
    }

    private void setUrlEvent(JLabel label, String url) {
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                BrowserUtil.browse(url);
            }
        });
    }

    @Override
    public void pack() {
        super.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = getWidth();
        int h = getHeight();
        int x = screen.width / 2 - w / 2;
        int y = screen.height / 2 - h;
        setLocation(x, y);
        setPreferredSize(new Dimension(w, h));
    }
}
