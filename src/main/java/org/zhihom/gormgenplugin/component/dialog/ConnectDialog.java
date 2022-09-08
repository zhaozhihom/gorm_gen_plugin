package org.zhihom.gormgenplugin.component.dialog;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.zhihom.gormgenplugin.component.MetaData;
import org.zhihom.gormgenplugin.db.Conn;
import org.zhihom.gormgenplugin.model.DatabaseModel;
import org.zhihom.gormgenplugin.model.TableModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectDialog extends DialogWrapper {

    private static final Logger LOG = Logger.getInstance(ConnectDialog.class);
    private JPanel contentPane;
    private JTextField host;
    private JTextField username;
    private JPasswordField password;
    private JTextField database;
    private JFormattedTextField port;

    private static final String url = "jdbc:mysql://%s:%s/%s";

    public ConnectDialog() {
        super(true);

        setOKButtonText("Add");
        setCancelButtonText("Cancel");

        init();
    }

    @Override
    protected void doOKAction() {
        String hostText = this.host.getText();
        String portText = this.port.getText();
        String databaseText = this.database.getText();
        String usernameText = this.username.getText();
        String passwordText = this.password.getText();
        Connection conn = null;
        try {
            List<String> tableNames = Conn.GetTableFromDBConn(hostText, portText, databaseText, usernameText, passwordText);
            List<TableModel> tables = new ArrayList<>();
            for (String tableName : tableNames) {
                tables.add(new TableModel(tableName, ""));
            }
            DatabaseModel databaseModel = new DatabaseModel();
            databaseModel.setHost(hostText);
            databaseModel.setPort(portText);
            databaseModel.setDatabase(databaseText);
            databaseModel.setUsername(usernameText);
            databaseModel.setPassword(passwordText);
            databaseModel.setTables(tables);
            MetaData.DB_MODELS.addElement(databaseModel);
        } catch (RuntimeException e) {
            LOG.error("connect to database err:", e);
            Messages.showErrorDialog("无法连接到数据库，请重新输入：" + e.getMessage(), "连接失败!");
            return;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        super.doOKAction();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }
}
