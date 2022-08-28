package org.bytedance.gormgenplugin.component.dialog;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.bytedance.gormgenplugin.component.MetaData;
import org.bytedance.gormgenplugin.model.DatabaseModel;
import org.bytedance.gormgenplugin.model.TableModel;
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

        setOKButtonText("添加");
        setCancelButtonText("取消");

        init();
    }

    @Override
    protected void doOKAction() {
        String hostText = this.host.getText();
        String portText = this.port.getText();
        String databaseText = this.database.getText();
        String usernameText = this.username.getText();
        String passwordText = this.password.getText();
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            Connection conn = DriverManager.getConnection(String.format(url, hostText, portText, databaseText), usernameText, passwordText);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("Show tables");
            List<TableModel> tables = new ArrayList<>();
            while(rs.next()) {
                String tableName = rs.getString(1);
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
        } catch (SQLException e) {
            LOG.error("connect to database err:", e);
            Messages.showErrorDialog("无法连接到数据库，请重新输入：" + e.getMessage(), "连接失败!");
            return;
        }
        super.doOKAction();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }
}
