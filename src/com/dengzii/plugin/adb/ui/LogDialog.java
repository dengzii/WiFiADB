package com.dengzii.plugin.adb.ui;

import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LogDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JTextArea textArea1;

    public LogDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonCancel);
        buttonCancel.addActionListener(e -> onCancel());
        textArea1.setFont(UIUtil.getLabelFont());
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

    public void show(String log) {
        textArea1.setText(log);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 700;
        int h = 500;
        int x = screen.width / 2 - w / 2;
        int y = screen.height / 2 - h / 2;
        setLocation(x, y);
        setPreferredSize(new Dimension(w, h));

        setTitle("WiFiAdb Tools");
        pack();
        setVisible(true);
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
