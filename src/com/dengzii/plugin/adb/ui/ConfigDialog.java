package com.dengzii.plugin.adb.ui;

import com.dengzii.plugin.adb.Config;
import com.dengzii.plugin.adb.DialogConfig;
import com.dengzii.plugin.adb.tools.ui.XDialog;

import javax.swing.*;
import java.awt.*;
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
public class ConfigDialog extends XDialog {
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
    private final List<JCheckBox> checkBoxes = new ArrayList<>();
    private final DialogConfig dialogConfig = DialogConfig.Companion.getINSTANCE();

    private ConfigDialog(CallBack callBack) {
        super("Custom Column");
        setContentPane(contentPane);

        checkBoxes.add(sn);
        checkBoxes.add(modelName);
        checkBoxes.add(name);
        checkBoxes.add(ip);
        checkBoxes.add(port);
        checkBoxes.add(status);
        checkBoxes.add(mark);
        checkBoxes.add(operate);

        checkBoxes.forEach(jCheckBox -> {
            DialogConfig.ColumnEnum columnEnum = DialogConfig.ColumnEnum.valueOf(jCheckBox.getText().toUpperCase());
            jCheckBox.setSelected(dialogConfig.getCol().contains(columnEnum));
        });

        applyButton.addActionListener(e -> {
            List<DialogConfig.ColumnEnum> columnEnums = new ArrayList<>();
            checkBoxes.forEach(jCheckBox -> {
                if (jCheckBox.isSelected()) {
                    DialogConfig.ColumnEnum columnEnum = DialogConfig.ColumnEnum.valueOf(jCheckBox.getText().toUpperCase());
                    columnEnums.add(columnEnum);
                }
            });
            DialogConfig.Companion.getINSTANCE().setCol(columnEnums);
            Config.INSTANCE.saveDialogConfig(DialogConfig.Companion.getINSTANCE());
            callBack.onApply();
            dispose();
        });
    }

    public static void createAndShow(CallBack callBack) {
        new ConfigDialog(callBack).packAndShow();
    }

    @Override
    public void pack() {
        super.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = getWidth();
        int h = getHeight();
        int x = 400 - w / 2;
        int y = screen.height / 2 - h;
        setLocation(x, y);
        setPreferredSize(new Dimension(w, h));
    }

    interface CallBack {
        void onApply();
    }
}
