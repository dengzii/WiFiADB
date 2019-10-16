package com.dengzii.plugin.adb.ui;

import com.dengzii.plugin.adb.Config;
import com.dengzii.plugin.adb.XLog;
import com.dengzii.plugin.adb.utils.AdbUtils;
import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class AdbDialog extends JDialog {

    private static final String ABOUT =
            "\n********************\n" +
                    "AdbWiFi Tool\n" +
                    "(c) dengzii 2019 \n" +
                    "GitHub: https://github.com/MrDenua/WiFiADB\n" +
                    "********************\n";

    private JPanel contentPane;
    private JTable table1;
    private JButton buttonRefresh;

    private DeviceTableModel deviceTableModel;

    private static final int TABLE_ROW_HEIGHT = 26;

    public AdbDialog() {
        setContentPane(contentPane);
        setModal(true);

        initDialog();
        initTable();
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
        initMenu();
    }

    private void initMenu() {

        JMenuBar menuBar = new JMenuBar();
        String[] menus = {"Tools", "About"};
        String[][] menuItems = {{"Log", "Clear All", "Refresh", "Connect Manual", "Exit"}, {"About"}};

        for (int i = 0; i < menus.length; i++) {
            JMenu menu = new JMenu(menus[i]);
            for (int j = 0; j < menuItems[i].length; j++) {
                JMenuItem menuItem = new JMenuItem(menuItems[i][j]);
                menu.add(menuItem);
                menuItem.addMouseListener(new SimpleMouseListener() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        onMenuClick(((JMenuItem) e.getSource()).getText());
                    }
                });
            }
            menuBar.add(menu);
        }
        setJMenuBar(menuBar);
    }

    private void onMenuClick(String title) {
        switch (title) {
            case "Log":
                new LogDialog().show(XLog.INSTANCE.getLog());
                break;
            case "Refresh":
                update();
                break;
            case "Clear All":
                Config.INSTANCE.clear();
                break;
            case "Exit":
                dispose();
                break;
            case "Connect Manual":
                new ConnectDialog().show(this::update);
                break;
            case "About":
                new LogDialog().show(ABOUT);
                break;
        }
    }

    public static void main(String[] args) {
        AdbDialog dialog = new AdbDialog();
        dialog.update();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private abstract static class SimpleMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
