package org.zhihom.gormgenplugin.model;

import java.util.Objects;

public class TableModel {

    private String tableName;

    private String modelName;

    private Boolean selected = Boolean.FALSE;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public TableModel(String tableName, String modelName, Boolean selected) {
        this.tableName = tableName;
        this.modelName = modelName;
        this.selected = selected;
    }

    public TableModel() {
    }

    public TableModel(String tableName, String modelName) {
        this.tableName = tableName;
        this.modelName = modelName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableModel that = (TableModel) o;
        return Objects.equals(tableName, that.tableName) && Objects.equals(modelName, that.modelName) && Objects.equals(selected, that.selected);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, modelName, selected);
    }
}
