package com.dengzii.plugin.adb.ui;

import com.dengzii.plugin.adb.Config;
import com.dengzii.plugin.adb.DialogConfig;
import com.dengzii.plugin.adb.XLog;
import com.dengzii.plugin.adb.utils.AdbUtils;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyEvent;

public class AdbDialog extends JDialog implements Runnable {

    private JPanel contentPane;
    private JTable table1;
    private JButton buttonRefresh;
    private JLabel labelStatus;

    private DeviceTableModel deviceTableModel;

    private static final int TABLE_ROW_HEIGHT = 26;

    public AdbDialog() {
        setContentPane(contentPane);
        setModal(true);

        initDialog();
        initTable();
        buttonRefresh.addActionListener(e -> update());
    }

    @Override
    public void run() {
        deviceTableModel.setData(AdbUtils.INSTANCE.getConnectedDeviceList());
        deviceTableModel.fireTableStructureChanged();
        initOperateCol();
        setStatus("Refresh complete");
    }

    public void setStatus(String status) {
        labelStatus.setText(status);
    }

    public void update() {
        setStatus("Refreshing, please wait...");
        new Thread(this).start();
    }

    public void update2() {
        deviceTableModel.setData(AdbUtils.INSTANCE.getConnectedDeviceList());
        deviceTableModel.fireTableStructureChanged();
        initOperateCol();
        setStatus("Refresh complete");
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
        int w = screen.width / 2;
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
        initMenu();
    }

    private void initMenu() {

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(getMain().getMenu());
        menuBar.add(getAdb().getMenu());
        menuBar.add(getSettings().getMenu());
        menuBar.add(getAbout().getMenu());
        setJMenuBar(menuBar);
    }

    private Menu getMain() {
        Menu tools = new Menu("Main");
        tools.addItem("Log", () -> new LogDialog().show(XLog.INSTANCE.getLog()));
        tools.addItem("Clear All", Config.INSTANCE::clear);
        tools.addItem("Refresh", this::update);
        tools.addItem("Connect Manual", () -> new ConnectDialog().show(this::update));
        tools.addItem("Exit", this::dispose);
        return tools;
    }

    private Menu getAdb() {
        Menu Adb = new Menu("ADB");
        Adb.addItem("Restart Server", () -> {
            AdbUtils.INSTANCE.restartServer();
            this.update();
        });
        Adb.addItem("Kill Server", () -> {
            AdbUtils.INSTANCE.killServer();
            this.update();
        });
        Adb.addItem("Start Server", () -> {
            AdbUtils.INSTANCE.startServer();
            this.update();
        });
        return Adb;
    }

    private Menu getSettings() {
        Menu settings = new Menu("Settings");
        settings.addItem("Custom Column", () -> {

        });
        settings.addItem("Reset Default", () -> {
            Config.INSTANCE.saveDialogConfig(new DialogConfig());
            this.initTable();
            this.update();
        });
        return settings;
    }

    private Menu getAbout() {
        Menu settings = new Menu("About");
        settings.addItem("About Tools", LogDialog::showAbout);
        settings.addItem("Github", () -> {
        });
        return settings;
    }

    public static void main(String[] args) {
        AdbDialog dialog = new AdbDialog();
        dialog.update();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


}
