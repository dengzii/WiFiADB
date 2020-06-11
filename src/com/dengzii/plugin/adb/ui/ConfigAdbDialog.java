package com.dengzii.plugin.adb.ui;

import com.dengzii.plugin.adb.Config;
import com.dengzii.plugin.adb.utils.AdbUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ConfigAdbDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField textField1;
    private JLabel label;

    public ConfigAdbDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public static void createAndShow() {
        new ConfigAdbDialog().init();
    }

    private void onOK() {
        File path = new File(textField1.getText());
        if (!path.exists()) {
            textField1.setText("");
            return;
        }
        if (path.isDirectory()) {
            path = new File(path.getAbsolutePath() + File.separator + "adb");
        }
        Config.INSTANCE.saveAdbPath(path.getAbsolutePath());
        AdbUtils.INSTANCE.reloadAdbPath();
        dispose();
    }

    private void init() {
        label.setText("Adb command is unavailable, please input adb path");
        textField1.setText(Config.INSTANCE.loadAdbPath());
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 360;
        int h = 180;
        int x = 400 - w / 2;
        int y = screen.height / 2 - h;
        setLocation(x, y);
        setPreferredSize(new Dimension(w, h));

        setTitle("Configure adb path");
        pack();
        setVisible(true);
    }
}
