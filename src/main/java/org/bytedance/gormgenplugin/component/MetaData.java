package org.bytedance.gormgenplugin.component;

import org.bytedance.gormgenplugin.model.DatabaseListModel;
import org.bytedance.gormgenplugin.model.DatabaseModel;
import org.bytedance.gormgenplugin.model.DatabaseTableModel;
import org.bytedance.gormgenplugin.service.DataService;

import java.util.ArrayList;

public class MetaData {

    public static final DataService dataService = DataService.getInstance();
    public static final DatabaseListModel<DatabaseModel> DB_MODELS = new DatabaseListModel(dataService.dbModels);
    public static final DatabaseTableModel TABLE_MODELS = new DatabaseTableModel(new ArrayList<>());

}
