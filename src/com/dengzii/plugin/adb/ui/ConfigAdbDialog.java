package com.dengzii.plugin.adb.ui;

import com.dengzii.plugin.adb.Config;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.actionSystem.impl.PresentationFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public class ConfigAdbDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField textField1;
    private JLabel label;
    private JPanel panelField;
    private boolean lookAdb;

    public ConfigAdbDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public static void createAndShow(boolean lookAdb) {
        ConfigAdbDialog d = new ConfigAdbDialog();
        d.lookAdb = lookAdb;
        d.init();
    }

    private void onOK() {
        File path = new File(textField1.getText());
        if (!path.exists()) {
            textField1.setText("");
            return;
        }
        if (path.isDirectory()) {
            path = new File(path.getAbsolutePath() + File.separator + "adb");
        }
        Config.INSTANCE.saveAdbPath(path.getAbsolutePath());
        dispose();
    }

    private void init() {
        textField1.setText(Config.INSTANCE.loadAdbPath());
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 360;
        int h = 180;
        int x = 400 - w / 2;
        int y = screen.height / 2 - h;
        setLocation(x, y);
        setPreferredSize(new Dimension(w, h));

        setTitle("Configure adb path");
        if (!lookAdb) {
            label.setText("Adb Path");
        } else {
            label.setText("Adb command is unavailable, please input adb path");
        }
        PresentationFactory factory = new PresentationFactory();
        AnAction action = new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                chooseAdb();
            }
        };
        Presentation p = factory.getPresentation(action);
        p.setText("Browser File");
        p.setIcon(AllIcons.General.OpenDisk);
        ActionButton actionButton = new ActionButton(action, p, "", new Dimension(26, 24));
        panelField.add(actionButton, BorderLayout.EAST);
        pack();
        setVisible(true);
    }

    private void chooseAdb() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setDialogTitle("Select adb executable file.");

        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return f.isDirectory() || name.equals("adb") || name.equals("adb.exe");
            }

            @Override
            public String getDescription() {
                return "adb executable file";
            }
        };
        fc.setFileFilter(filter);
        int f = fc.showOpenDialog(panelField);
        if (f == JFileChooser.APPROVE_OPTION) {
            textField1.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }
}
