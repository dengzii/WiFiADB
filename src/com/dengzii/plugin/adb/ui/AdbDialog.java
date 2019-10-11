package com.dengzii.plugin.adb.ui;

import com.dengzii.plugin.adb.Config;
import com.dengzii.plugin.adb.XLog;
import com.dengzii.plugin.adb.utils.AdbUtils;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyEvent;

public class AdbDialog extends JDialog {

    private JPanel contentPane;
    private JTable table1;
    private JButton buttonExit;
    private JButton buttonLog;
    private JButton buttonClean;
    private JButton buttonRefresh;

    private DeviceTableModel deviceTableModel;

    private static final int TABLE_ROW_HEIGHT = 26;

    public AdbDialog() {
        setContentPane(contentPane);
        setModal(true);

        initDialog();
        initTable();

        buttonLog.addActionListener(e -> new LogDialog().show(XLog.INSTANCE.getLog()));
        buttonExit.addActionListener(e -> dispose());
        buttonClean.addActionListener(e -> Config.INSTANCE.clear());
        buttonRefresh.addActionListener(e -> update());
    }

    public void update() {

        deviceTableModel.setData(AdbUtils.INSTANCE.getConnectedDeviceList());
        deviceTableModel.fireTableStructureChanged();
        initOperateCol();
    }

    private void initTable() {

        deviceTableModel = new DeviceTableModel();
        deviceTableModel.setColumnCount(7);
        table1.setModel(deviceTableModel);
        table1.setRowHeight(TABLE_ROW_HEIGHT);
        table1.setColumnSelectionAllowed(false);
        table1.setRowSelectionAllowed(false);
    }

    private void initOperateCol() {
        table1.getColumnModel()
                .getColumn(6)
                .setCellEditor(new ButtonEditor(this));
        TableColumn column = table1.getColumnModel().getColumn(6);
        column.setCellRenderer(new ButtonEditor(this));
    }

    private void initDialog() {
        Dimension screen = getToolkit().getScreenSize();
        int w = screen.width / 3;
        int h = 300;
        int x = screen.width / 2 - w / 2;
        int y = screen.height / 2 - h;
        setLocation(x, y);
        contentPane.setPreferredSize(new Dimension(w, h));

        setTitle("WiFiADB Tool");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        contentPane.registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void main(String[] args) {
        AdbDialog dialog = new AdbDialog();
        dialog.update();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
