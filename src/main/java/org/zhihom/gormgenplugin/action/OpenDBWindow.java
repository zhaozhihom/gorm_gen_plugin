package org.zhihom.gormgenplugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.zhihom.gormgenplugin.component.dialog.DatabaseDialog;
import org.jetbrains.annotations.NotNull;

public class OpenDBWindow extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DatabaseDialog dbDialog = new DatabaseDialog(e.getProject());
        boolean b = dbDialog.showAndGet();
    }
}

