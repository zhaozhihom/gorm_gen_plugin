package org.zhihom.gormgenplugin.model;

import java.util.List;
import java.util.Objects;

public class DatabaseModel {

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    private List<TableModel> tables;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<TableModel> getTables() {
        return tables;
    }

    public void setTables(List<TableModel> tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        return this.database;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseModel that = (DatabaseModel) o;
        return Objects.equals(host, that.host) && Objects.equals(port, that.port) && Objects.equals(database, that.database) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(tables, that.tables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, database, username, password, tables);
    }
}
