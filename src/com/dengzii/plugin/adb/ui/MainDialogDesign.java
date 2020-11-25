package com.dengzii.plugin.adb.ui;

import com.dengzii.plugin.adb.tools.ui.XDialog;

import javax.swing.*;

public class MainDialogDesign extends XDialog {

    JPanel contentPane;
    JTable deviceTable;
    JButton buttonRefresh;
    JLabel labelStatus;

    MainDialogDesign() {
        super("WiFiADB Tools");
        setContentPane(contentPane);
    }
}
