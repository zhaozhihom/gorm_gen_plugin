package org.bytedance.gormgenplugin.component;

import org.bytedance.gormgenplugin.model.DatabaseModel;
import org.bytedance.gormgenplugin.model.DatabaseListModel;
import org.bytedance.gormgenplugin.model.DatabaseTableModel;
import org.bytedance.gormgenplugin.model.TableModel;
import org.bytedance.gormgenplugin.service.DataService;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class MetaData {

    public static final DataService dataService = DataService.getInstance();
    public static final DatabaseListModel<DatabaseModel> DB_MODELS = new DatabaseListModel(dataService.dbModels);
    public static final DatabaseTableModel TABLE_MODELS = new DatabaseTableModel(new ArrayList<>());

}
