package org.zhihom.gormgenplugin.component.dialog;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.CoreProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBList;
import org.apache.pdfbox.io.IOUtils;
import org.zhihom.gormgenplugin.component.MetaData;
import org.zhihom.gormgenplugin.db.Conn;
import org.zhihom.gormgenplugin.jna.GenerateService;
import org.zhihom.gormgenplugin.model.DatabaseModel;
import org.zhihom.gormgenplugin.model.TableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseDialog extends DialogWrapper {

    private static final Logger LOG = Logger.getInstance(DatabaseDialog.class);

    private static String urlFormat = "%s:%s@(%s:%s)/%s";
    private static String outPath = "dal/query";
    private JPanel contentPane;
    private JBList dbList;
    private JTable dbTable;
    private JButton buttonNewConnect;
    private JButton buttonRefresh;

    private GenerateService generateService = new GenerateService();

    private Project project;


    public DatabaseDialog(Project project) {
        super(true);

        this.project = project;
        buttonNewConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddConnect();
            }
        });

        buttonRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRefreshConnect();
            }
        });

        dbList.setModel(MetaData.DB_MODELS);
        dbList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()){
                    DatabaseModel currentDatabaseModel = getCurrentDatabaseModel();
                    if (currentDatabaseModel != null) {
                        MetaData.TABLE_MODELS.setTableModelList(currentDatabaseModel.getTables());
                    }
                }
            }
        });

        MetaData.TABLE_MODELS.setTableModelList(new ArrayList<>());
        dbTable.setModel(MetaData.TABLE_MODELS);
        dbTable.setRowSelectionAllowed(false);

        setCancelButtonText("Cancel");
        setOKButtonText("Generate");

        init();

    }

    private void onAddConnect() {
        ConnectDialog connectDialog = new ConnectDialog();
        connectDialog.showAndGet();
    }

    private void onRefreshConnect() {
        DatabaseModel currDb = getCurrentDatabaseModel();
        List<TableModel> tableModels = currDb.getTables();
        try {
            List<String> tableNames = Conn.GetTableFromDBConn(currDb.getHost(), currDb.getPort(), currDb.getDatabase(),
                    currDb.getUsername(), currDb.getPassword());
            List<TableModel> newTableList = new ArrayList<>();
            Map<String, TableModel> map = new HashMap<>();
            for (TableModel tm : tableModels) {
                map.put(tm.getTableName(), tm);
            }
            for (String tableName : tableNames) {
                if (map.containsKey(tableName)) {
                    newTableList.add(map.get(tableName));
                } else {
                    TableModel tableModel = new TableModel(tableName, "");
                    newTableList.add(tableModel);
                }
            }
            currDb.setTables(newTableList);
            MetaData.TABLE_MODELS.setTableModelList(newTableList);
        } catch (RuntimeException e) {
            LOG.error("connect db err: ", e);
            Messages.showErrorDialog("connect to db failed!：" + e.getMessage(), "failed!");
            throw new RuntimeException(e);
        }
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }

    @Override
    protected void doOKAction() {
        Task.WithResult<Message, Exception> task = new Task.WithResult<>(project, "Generator", false) {
            @Override
            public Message getResult() throws Exception {
                return super.getResult();
            }

            @Override
            protected Message compute(@NotNull ProgressIndicator indicator) throws Exception {
                indicator.setText("Generating，Please waiting...");
                indicator.setIndeterminate(true);
                String absoluteOutPath = project.getBasePath() + "/" + outPath;

                DatabaseModel currDB = getCurrentDatabaseModel();
                if (currDB == null) {
                    return new Message("invalid param!", "Please choose a database!");
                }
                String url = String.format(urlFormat, currDB.getUsername(), currDB.getPassword(), currDB.getHost(), currDB.getPort(), currDB.getDatabase());

                List<TableModel> tableModelList = MetaData.TABLE_MODELS.getTableModelList();
                List<TableModel> tableModels = tableModelList.stream().filter(TableModel::getSelected).collect(Collectors.toList());
                if (tableModels.size() == 0) {
                    return new Message("invalid param!", "Please choose a table!");
                }
                StringBuilder tables = new StringBuilder();
                StringBuilder models = new StringBuilder();
                for (int i = 0; i < tableModels.size(); i++) {
                    TableModel tableModel = tableModels.get(i);
                    tables.append(tableModel.getTableName());
                    if (StringUtil.isNotEmpty(tableModel.getModelName())){
                        models.append(tableModel.getModelName());
                    }
                    if (i < tableModels.size() - 1) {
                        tables.append(",");
                        models.append(",");
                    }
                }
                String goModulePath = "";
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(project.getBasePath() + "/" + "go.mod");
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                    while ((goModulePath = bufferedReader.readLine()) != null) {
                        if (StringUtil.startsWith(goModulePath, "module")) {
                            goModulePath = goModulePath.replace("module", "").trim();
                            break;
                        }
                    }
                } catch (Exception e) {
                    LOG.error("generate code task err:", e);
                    return new Message("error", "go.mod:" + e.getMessage());
                } finally {
                    IOUtils.closeQuietly(fileInputStream);
                }

                String msg = generateService.gormGen(absoluteOutPath, url, tables.toString(), models.toString(), goModulePath);
                if (StringUtil.isNotEmpty(msg)) {
                    return new Message("error", "Generate failed:" + msg);
                } else {
                    return null;
                }
            }
        };

        try {
            CoreProgressManager.getInstance().run(task);
            if (task.getResult() != null) {
                Messages.showErrorDialog(task.getResult().msg, task.getResult().title);
                return;
            }

            project.save();
            Objects.requireNonNull(project.getBaseDir()).refresh(true, true);

        } catch (Exception e) {
            LOG.error("generate code task err:", e);
            Messages.showErrorDialog("生成失败！", "异常!");
            return;
        }

        super.doOKAction();

    }


    private DatabaseModel getCurrentDatabaseModel() {
        return MetaData.DB_MODELS.getElementAt(dbList.getSelectedIndex());
    }

    static class Message {
        public String title;
        public String msg;

        public Message(String title, String msg) {
            this.title = title;
            this.msg = msg;
        }
    }

}
