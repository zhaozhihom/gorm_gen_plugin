package org.zhihom.gormgenplugin.db;


import com.intellij.openapi.diagnostic.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Conn {

    private static final Logger LOG = Logger.getInstance(Conn.class);
    private static final String url = "jdbc:mysql://%s:%s/%s";

    public static List<String> GetTableFromDBConn(String host, String port, String database, String username, String password) throws RuntimeException {
        Connection conn = null;
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            conn = DriverManager.getConnection(String.format(url, host, port, database), username, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("Show tables");
            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
            return tables;
        } catch (SQLException e) {
            LOG.error("connect to database err:", e);
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
