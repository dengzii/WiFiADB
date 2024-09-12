package com.dengzii.plugin.adb.ui;

import com.dengzii.plugin.adb.tools.ui.XDialog;

import javax.swing.*;

public class ScanDialogDesign extends XDialog {
    public JTable tableResult;
    public JTextField fieldTimeoutPing;
    public JTextField filedTimeoutAdb;
    public JTextField fieldThreadNum;
    public JTextField fieldPortStart;
    public JTextField fieldPortEnd;
    public JTextField fieldIpStart;
    public JTextField fieldIpEnd;
    public JButton buttonScan;
    public JLabel labelProgress;
    public JLabel labelProcessor;
    public JPanel content;
    public JLabel labelIpStart;
    public JLabel labelIpEnd;
    public JLabel labelIp;
    public JComboBox<String> comboBoxInterface;

    ScanDialogDesign(){
        super("Scan Device [beta]");
        setContentPane(content);
    }
}
