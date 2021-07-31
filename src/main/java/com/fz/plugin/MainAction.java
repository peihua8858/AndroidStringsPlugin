package com.fz.plugin;

import com.fz.plugin.configs.Configs;
import com.fz.plugin.ui.ToolsFrame;
import com.fz.plugin.utils.FileUtils;
import com.fz.plugin.utils.Utils;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * string.xml解析成json数据
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/7/17 09:27
 */
public class MainAction extends BaseGenerateAction {

    public MainAction() {
        super(null);
    }

    protected MainAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = getProjectFromContext(event);
        if (project == null) {
            return;
        }
        String projectPath = project.getBasePath();
        if (StringUtils.isEmpty(projectPath)) {
            return;
        }
        File rootDir = new File(projectPath);
        Utils.runWithNotification(() -> {
            List<String> excelFiles = FileUtils.scanFiles(rootDir, Configs.PROJECT_EXCEL_FILE);
            List<String> moduleFiles = FileUtils.scanFolder(rootDir, Configs.PROJECT_MODULE_SRC_FOLDER,
                    "**/.*/**,**/*build*/**");
            Utils.invokeLater(() -> {
                ToolsFrame toolsSettings = new ToolsFrame(rootDir, project, excelFiles, moduleFiles);
                toolsSettings.setVisible(true);
            });

        }, project);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Project project = getProjectFromContext(event);
        event.getPresentation().setEnabled(project != null);
    }

    private Project getProjectFromContext(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (project == null) {
            return null;
        }
        String projectPath = project.getBasePath();
        if (StringUtils.isEmpty(projectPath)) {
            return null;
        }
        File projectFile = new File(projectPath);
        if (!projectFile.exists()) {
            return null;
        }
        return project;
    }
}
