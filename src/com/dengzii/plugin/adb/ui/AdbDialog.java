package com.dengzii.plugin.adb.ui;

import com.dengzii.plugin.adb.Config;
import com.dengzii.plugin.adb.Device;
import com.dengzii.plugin.adb.DialogConfig;
import com.dengzii.plugin.adb.XLog;
import com.dengzii.plugin.adb.utils.AdbUtils;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class AdbDialog extends JDialog {

    private JPanel contentPane;
    private JTable deviceTable;
    private JButton buttonRefresh;
    private JLabel labelStatus;

    private DeviceTableModel deviceTableModel;
    private DialogConfig dialogConfig;
    private List<Device> deviceList;

    public AdbDialog() {
        setContentPane(contentPane);
        setModal(false);

        contentPane.registerKeyboardAction(
                e -> {
                    persistStatus();
                    dispose();
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                persistStatus();
                dispose();
            }
        });
        dialogConfig = DialogConfig.Companion.getINSTANCE();
        initDialog();
        buttonRefresh.addActionListener(e -> updateTable());
    }

    public void setStatus(String status) {
        labelStatus.setText(status);
    }

    public void updateTable() {
        setStatus("Refreshing, please wait...");
        new Thread(this::updateTableOnUi).start();
    }

    public void updateTableOnUi() {
        deviceList = AdbUtils.INSTANCE.getConnectedDeviceList();
        deviceTableModel.setData(deviceList);
        deviceTableModel.fireTableStructureChanged();
        initColumn();
        setStatus("Refresh complete");
    }

    private void initTable() {

        deviceTableModel = new DeviceTableModel();
        deviceTableModel.setColumnCount(dialogConfig.getCol().size());
        deviceTable.setModel(deviceTableModel);
        deviceTable.setRowHeight(DialogConfig.ROW_HEIGHT);
        deviceTable.setColumnSelectionAllowed(false);
        deviceTable.setRowSelectionAllowed(false);
        initColumn();
    }

    private void initColumn() {
        deviceTableModel.fireTableStructureChanged();
        // setup table column width from persisted status
        for (int i = 0; i < dialogConfig.getCol().size(); i++) {
            TableColumn tableColumn = deviceTable.getColumnModel().getColumn(i);
            String colName = dialogConfig.getCol().get(i).name().toLowerCase();
            int width = dialogConfig.getColWidth().getOrDefault(colName, 0);
            if (width != 0) {
                tableColumn.setPreferredWidth(width);
            }
        }
        int buttonCol = dialogConfig.getCol().indexOf(DialogConfig.COL.OPERATE);
        if (buttonCol < 0) return;
        deviceTable.getColumnModel()
                .getColumn(buttonCol)
                .setCellEditor(new ButtonEditor(this));
        TableColumn column = deviceTable.getColumnModel().getColumn(buttonCol);
        column.setCellRenderer(new ButtonEditor(this));
    }

    private void initDialog() {
        Dimension screen = getToolkit().getScreenSize();
        int w = dialogConfig.getWidth() == 0 ? screen.width / 2 : dialogConfig.getWidth();
        int h = dialogConfig.getHeight() == 0 ? 300 : dialogConfig.getHeight();
        int x = dialogConfig.getX() == 0 ? screen.width / 2 - w / 2 : dialogConfig.getX();
        int y = dialogConfig.getY() == 0 ? screen.height / 2 - h : dialogConfig.getY();
        setLocation(x, y);
        contentPane.setPreferredSize(new Dimension(w, h));

        setTitle("WiFiADB Tool");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        contentPane.registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        initMenu();
        initTable();
    }

    private void persistStatus() {
        dialogConfig.setHeight(getHeight());
        dialogConfig.setWidth(getWidth());
        dialogConfig.setX(getX());
        dialogConfig.setY(getY());

        int markColIndex = -1;
        try {
            for (int i = 0; i < dialogConfig.getCol().size(); i++) {
                TableColumn column = deviceTable.getColumnModel().getColumn(i);
                DialogConfig.COL c = DialogConfig.COL.valueOf(column.getHeaderValue().toString().toUpperCase());
                if (c.equals(DialogConfig.COL.MARK)) {
                    markColIndex = i;
                }
                dialogConfig.getColWidth().put(c.name().toLowerCase(), column.getWidth());
                dialogConfig.getCol().remove(c);
                dialogConfig.getCol().add(c);
            }
            Config.INSTANCE.saveDialogConfig(dialogConfig);

            if (markColIndex >= 0) {
                for (int i = 0; i < deviceList.size(); i++) {
                    String mark = deviceTable.getValueAt(i, markColIndex).toString();
                    deviceList.get(i).setMark(mark);
                    System.out.println(deviceList.get(i));
                }
            }
            Config.INSTANCE.saveDevice(deviceList);
        } catch (Throwable t) {
            XLog.INSTANCE.e("AdbDialog.persistStatus", t);
        }
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
        tools.addItem("Refresh", this::updateTable);
        tools.addItem("Connect Manual", () -> new ConnectDialog().show(this::updateTable));
        tools.addItem("Exit", this::dispose);
        return tools;
    }

    private Menu getAdb() {
        Menu Adb = new Menu("ADB");
        Adb.addItem("Restart Server", () -> {
            AdbUtils.INSTANCE.restartServer();
            this.updateTable();
        });
        Adb.addItem("Kill Server", () -> {
            AdbUtils.INSTANCE.killServer();
            this.updateTable();
        });
        Adb.addItem("Start Server", () -> {
            AdbUtils.INSTANCE.startServer();
            this.updateTable();
        });
        return Adb;
    }

    private Menu getSettings() {
        Menu settings = new Menu("Settings");
        settings.addItem("Custom Column", () -> {
            ConfigDialog.create(() -> {
                initTable();
                updateTable();
                initColumn();
            });
        });
        settings.addItem("Reset Default", () -> {
            Config.INSTANCE.saveDialogConfig(new DialogConfig());
            dialogConfig = Config.INSTANCE.loadDialogConfig();
            initTable();
            updateTable();
            initColumn();
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
        dialog.updateTable();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


}
