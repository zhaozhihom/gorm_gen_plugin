package org.bytedance.gormgenplugin.component.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import org.bytedance.gormgenplugin.component.MetaData;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.util.ArrayList;

public class DatabaseDialog extends DialogWrapper {
    private JPanel contentPane;
    private JBList dbList;
    private JTable dbTable;
    private JButton buttonNewConnect;


    public DatabaseDialog() {
        super(true);

        buttonNewConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddConnect();
            }
        });

        dbList.setModel(MetaData.DB_MODELS);
        dbList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()){
                    int selectedIndex = dbList.getSelectedIndex();
                    System.out.println(selectedIndex);
                    MetaData.TABLE_MODELS.setTableModelList(MetaData.DB_MODELS.getElementAt(selectedIndex).getTables());
                }
            }
        });

        MetaData.TABLE_MODELS.setTableModelList(new ArrayList<>());
        dbTable.setModel(MetaData.TABLE_MODELS);
        dbTable.setRowSelectionAllowed(false);

        setCancelButtonText("取消");
        setOKButtonText("生成");


        init();

    }

    private void onAddConnect() {
        ConnectDialog connectDialog = new ConnectDialog();
        boolean b = connectDialog.showAndGet();
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }

}
