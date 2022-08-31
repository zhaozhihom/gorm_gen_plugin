package org.zhihom.gormgenplugin.component;

import org.zhihom.gormgenplugin.model.DatabaseListModel;
import org.zhihom.gormgenplugin.model.DatabaseModel;
import org.zhihom.gormgenplugin.model.DatabaseTableModel;
import org.zhihom.gormgenplugin.service.DataService;

import java.util.ArrayList;

public class MetaData {

    public static final DataService dataService = DataService.getInstance();
    public static final DatabaseListModel<DatabaseModel> DB_MODELS = new DatabaseListModel(dataService.dbModels);
    public static final DatabaseTableModel TABLE_MODELS = new DatabaseTableModel(new ArrayList<>());

}
