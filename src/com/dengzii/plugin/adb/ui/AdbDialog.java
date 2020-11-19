package com.dengzii.plugin.adb.ui;

import com.dengzii.plugin.adb.tools.ui.XDialog;

import javax.swing.*;

public class AdbDialog extends XDialog {

    JPanel contentPane;
    JTable deviceTable;
    JButton buttonRefresh;
    JLabel labelStatus;

    AdbDialog() {
        super("WiFiADB Tools");
        setContentPane(contentPane);
    }
}
