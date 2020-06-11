package com.dengzii.plugin.adb.ui;

import com.dengzii.plugin.adb.utils.AdbUtils;
import com.dengzii.plugin.adb.utils.CmdResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ConnectDialog extends JDialog implements Runnable {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldIp;
    private JTextField textFieldPort;
    private CallBack callBack;

    public ConnectDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void main(String[] args) {
        ConnectDialog dialog = new ConnectDialog();
        dialog.show(() -> {

        });
        System.exit(0);
    }

    public void show(CallBack callBack) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 260;
        int h = 180;
        int x = 400 - w / 2;
        int y = screen.height / 2 - h;
        setLocation(x, y);
        setPreferredSize(new Dimension(w, h));

        this.callBack = callBack;
        setTitle("Connect to device");
        pack();
        setVisible(true);
    }

    @Override
    public void run() {
        CmdResult result = AdbUtils.INSTANCE.connect(textFieldIp.getText(), textFieldPort.getText());
        callBack.callBack();
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
        void callBack();
    }
}
