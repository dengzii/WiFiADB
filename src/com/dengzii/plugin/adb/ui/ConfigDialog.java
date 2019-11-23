package com.dengzii.plugin.adb.ui;

import com.dengzii.plugin.adb.Config;
import com.dengzii.plugin.adb.DialogConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/11/23
 * desc   :
 * </pre>
 */
public class ConfigDialog extends JDialog {
    private JPanel contentPane;
    private JButton applyButton;
    private JCheckBox status;
    private JCheckBox mark;
    private JCheckBox operate;
    private JCheckBox port;
    private JCheckBox sn;
    private JCheckBox modelName;
    private JCheckBox name;
    private JCheckBox ip;

    private List<JCheckBox> checkBoxes = new ArrayList<>();
    private DialogConfig dialogConfig = DialogConfig.Companion.getINSTANCE();

    private ConfigDialog(CallBack callBack) {
        setContentPane(contentPane);
        setModal(false);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        contentPane.registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );
        checkBoxes.add(sn);
        checkBoxes.add(modelName);
        checkBoxes.add(name);
        checkBoxes.add(ip);
        checkBoxes.add(port);
        checkBoxes.add(status);
        checkBoxes.add(mark);
        checkBoxes.add(operate);

        checkBoxes.forEach(jCheckBox -> {
            DialogConfig.COL col = DialogConfig.COL.valueOf(jCheckBox.getText().toUpperCase());
            jCheckBox.setSelected(dialogConfig.getCol().contains(col));
        });

        applyButton.addActionListener(e -> {
            List<DialogConfig.COL> cols = new ArrayList<>();
            checkBoxes.forEach(jCheckBox -> {
                if (jCheckBox.isSelected()) {
                    DialogConfig.COL col = DialogConfig.COL.valueOf(jCheckBox.getText().toUpperCase());
                    cols.add(col);
                }
            });

            DialogConfig.Companion.getINSTANCE().setCol(cols);
            Config.INSTANCE.saveDialogConfig(DialogConfig.Companion.getINSTANCE());
            callBack.onApply();
            dispose();
        });
        setTitle("Custom Column");
    }

    @Override
    public void pack() {
        super.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = getWidth();
        int h = getHeight();
        int x = screen.width / 2 - w / 2;
        int y = screen.height / 2 - h;
        setLocation(x, y);
        setPreferredSize(new Dimension(w, h));
    }

    public static void create(CallBack callBack) {
        JDialog dialog = new ConfigDialog(callBack);
        dialog.pack();
        dialog.setVisible(true);
    }

    interface CallBack {
        void onApply();
    }
}
