package org.bytedance.gormgenplugin.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.bytedance.gormgenplugin.model.DatabaseModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(
        name = "org.bytedance.gormgenplugin.service.DataService",
        storages = {
                @Storage(value = "gormGenConfig.xml", roamingType = RoamingType.DISABLED)
        })
public class DataService implements PersistentStateComponent<DataService> {

    public List<DatabaseModel> dbModels = new ArrayList<>();

    public static DataService getInstance() {
        return ApplicationManager.getApplication().getService(DataService.class);
    }

    @Override
    public @Nullable DataService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DataService state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
