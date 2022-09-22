package org.zhihom.gormgenplugin.model;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class DatabaseTableModel extends AbstractTableModel {
    private List<TableModel> tableModelList;

    private final String[] columnNames = new String[] {
            "table", "model_name", "select"
    };
    private final Class[] columnClass = new Class[] {
            String.class, String.class, Boolean.class
    };

    public DatabaseTableModel(List<TableModel> tableModelList) {
        this.tableModelList = tableModelList;
    }

    @Override
    public String getColumnName(int column)
    {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return columnClass[columnIndex];
    }

    @Override
    public int getRowCount() {
        return tableModelList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TableModel tableModel = tableModelList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return tableModel.getTableName();
            case 1:
                return tableModel.getModelName();
            case 2:
                return tableModel.getSelected();
            default:
                return "";
        }
    }
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        TableModel tableModel = tableModelList.get(rowIndex);
        if (1 == columnIndex) {
            tableModel.setModelName((String) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        } else if (2 == columnIndex) {
            tableModel.setSelected((Boolean) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    public void setTableModelList(List<TableModel> tableModelList) {
        this.tableModelList = tableModelList;
        fireTableDataChanged();
    }

    public List<TableModel> getTableModelList() {
        return tableModelList;
    }
}
