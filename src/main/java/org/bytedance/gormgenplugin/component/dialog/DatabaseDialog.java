package org.bytedance.gormgenplugin.component.dialog;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.CoreProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBList;
import org.apache.pdfbox.io.IOUtils;
import org.bytedance.gormgenplugin.component.MetaData;
import org.bytedance.gormgenplugin.jna.GenerateService;
import org.bytedance.gormgenplugin.model.DatabaseModel;
import org.bytedance.gormgenplugin.model.TableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DatabaseDialog extends DialogWrapper {

    private static final Logger LOG = Logger.getInstance(DatabaseDialog.class);

    private static String urlFormat = "%s:%s@(%s:%s)/%s";
    private static String outPath = "dal/query";
    private JPanel contentPane;
    private JBList dbList;
    private JTable dbTable;
    private JButton buttonNewConnect;

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

        setCancelButtonText("取消");
        setOKButtonText("生成");

        init();

    }

    private void onAddConnect() {
        ConnectDialog connectDialog = new ConnectDialog();
        boolean b = connectDialog.showAndGet();
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
                indicator.setText("正在生成，请稍侯...");
                indicator.setIndeterminate(true);
                String absoluteOutPath = project.getBasePath() + "/" + outPath;

                DatabaseModel currDB = getCurrentDatabaseModel();
                if (currDB == null) {
                    return new Message("参数错误!", "请选择数据库！");
                }
                String url = String.format(urlFormat, currDB.getUsername(), currDB.getPassword(), currDB.getHost(), currDB.getPort(), currDB.getDatabase());

                List<TableModel> tableModelList = MetaData.TABLE_MODELS.getTableModelList();
                List<TableModel> tableModels = tableModelList.stream().filter(TableModel::getSelected).collect(Collectors.toList());
                if (tableModels.size() == 0) {
                    return new Message("参数错误!", "请选择表！");
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
                    Messages.showErrorDialog("go.mod文件格式错误！", "异常!");
                    return null;
                } finally {
                    IOUtils.closeQuietly(fileInputStream);
                }

                String msg = generateService.gormGen(absoluteOutPath, url, tables.toString(), models.toString(), goModulePath);
                if (StringUtil.isNotEmpty(msg)) {
                    return new Message("错误", "生成失败！ " + msg);
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
