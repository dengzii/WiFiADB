package com.dengzii.plugin.adb.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ConnectDialog extends JDialog implements Runnable {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldIp;
    private JTextField textFieldPort;
    private JCheckBox saveToListWhenCheckBox;
    private JLabel labelHint;
    private CallBack callBack;

    public ConnectDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());


        labelHint.setText("<html>Please ensure the ADB port is open, when disconnecting from debugging, the device may close the ADB port</html>");
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void main(String[] args) {
        ConnectDialog dialog = new ConnectDialog();
        dialog.show((s, ip, port) -> {

        });
        System.exit(0);
    }

    public void show(CallBack callBack) {


        this.callBack = callBack;
        setTitle("Connect to device");
        pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = getWidth();
        int h = getHeight();
        int x = screen.width / 2 - w / 2;
        int y = screen.height / 2 - h;
        setBounds(x, y, w, h);
        setVisible(true);
    }

    @Override
    public void run() {
        int port = 5555;
        try {
            port = Integer.parseInt(textFieldPort.getText());
        } catch (Exception ignored) {

        }
        callBack.callBack(saveToListWhenCheckBox.isSelected(), textFieldIp.getText(), port);
        dispose();
    }

    private void onOK() {
        new Thread(this).start();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    interface CallBack {
        void callBack(boolean save, String ip, int port);
    }
}
