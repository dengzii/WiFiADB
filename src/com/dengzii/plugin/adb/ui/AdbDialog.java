package com.dengzii.plugin.adb.ui;

import com.dengzii.plugin.adb.Device;
import com.dengzii.plugin.adb.XLog;
import com.dengzii.plugin.adb.utils.AdbUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class AdbDialog extends JDialog {

    private JPanel contentPane;
    private JTable table1;
    private JTextArea textArea1;

    private DeviceTableModel deviceTableModel;

    private static final int TABLE_ROW_HEIGHT = 26;
    private static final String[] TABLE_HEADER = {"Name", "Model_Name", "Model", "IP", "Port", "Status", "Operate"};

    public AdbDialog() {
        setContentPane(contentPane);
        setModal(true);

        initDialog();
        initTable();
    }

    public void setData(List<Device> d) {
        deviceTableModel.setData(d);
        table1.invalidate();
    }

    private void initTable() {

        deviceTableModel = new DeviceTableModel();
        table1.getDefaultRenderer(TableOperateCellRenderer.class);
        table1.setModel(deviceTableModel);
        table1.invalidate();
        table1.setRowHeight(TABLE_ROW_HEIGHT);
        table1.setColumnSelectionAllowed(false);
        table1.setRowSelectionAllowed(false);

        setupOperateCol();
        TableColumnModel origin = table1.getTableHeader().getColumnModel();
        table1.getTableHeader().setColumnModel(new TableHeaderModel(origin, TABLE_HEADER));
    }

    private void setupOperateCol() {
        table1.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor());
        TableColumn column = table1.getColumnModel().getColumn(6);
        column.setCellRenderer(new TableOperateCellRenderer());
    }

    private void initDialog() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = screen.width / 3;
        int h = 300;
        int x = screen.width / 2 - w / 2;
        int y = screen.height / 2 - h;
        setLocation(x, y);
        setPreferredSize(new Dimension(w, h));

        setTitle("WiFiADB Tool");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        contentPane.registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private static class TableHeaderModel extends DefaultTableColumnModel {

        private String[] header;
        private TableColumnModel originModel;

        TableHeaderModel(TableColumnModel origin, String[] header) {
            this.header = header;
            this.originModel = origin;
        }

        @Override
        public void moveColumn(int columnIndex, int newIndex) {
            originModel.moveColumn(columnIndex, newIndex);
        }

        @Override
        public void removeColumn(TableColumn column) {
            originModel.removeColumn(column);
        }

        @Override
        public int getColumnCount() {
            return originModel.getColumnCount();
        }

        @Override
        public TableColumn getColumn(int columnIndex) {
            TableColumn tableColumn = originModel.getColumn(columnIndex);
            tableColumn.setHeaderValue(header[columnIndex]);
            return tableColumn;
        }
    }

    public static void main(String[] args) {
        AdbDialog dialog = new AdbDialog();
//        dialog.setData(new ArrayList<Device>() {{
//            Device device = new Device("DFE123122Xd", "HUAWEI");
//            device.setIp("192.169.9.2");
//            device.setStatus(Device.STATUS.CONNECTED);
//            device.setPort("5555");
//            device.setModelName("Mate40");
//            add(device);
//            Device device1 = new Device("DE1222Xd", "SONY");
//            device1.setIp("192.169.9.24");
//            device1.setStatus(Device.STATUS.DISCONNECT);
//            device1.setPort("5557");
//            device1.setModelName("Xperia1");
//            add(device1);
//        }});
        dialog.setData(AdbUtils.INSTANCE.getConnectedDeviceList());
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
