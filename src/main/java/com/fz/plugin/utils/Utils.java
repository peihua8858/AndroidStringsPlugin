package com.fz.plugin.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;

public class Utils {

    /**
     * 去掉前后双引号
     *
     * @param text
     * @author dingpeihua
     * @date 2019/7/19 20:01
     * @version 1.0
     */
    public static String removeDoubleQuotes(String text) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        if (text.startsWith("\"")) {
            text = text.substring(1);
        }
        if (text.endsWith("\"")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    public static void showMessageDialog(Project project, String message) {
        showMessageDialog(project, "提示", message);
    }

    public static void showMessageDialog(Project project, String title, String message) {
        invokeLater(() -> Messages.showMessageDialog(project, message,
                title, Messages.getInformationIcon()));
    }

    public static void invokeLater(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    public static void runWithNotification(
            final Runnable run, Project project) {
        runWithNotification(run, project, makeProgress("处理中，请稍后...",
                project, false, false, false));
    }

    public static void runWithNotification(
            final Runnable run, Project project, ProgressWindow progressWindow) {
        ApplicationManager.getApplication()
                .executeOnPooledThread(
                        () -> ProgressManager.getInstance().runProcess(run, progressWindow));
    }

    public static ProgressWindow makeProgress(
            String title,
            Project project,
            boolean cancelable,
            boolean hidable,
            boolean indeterminate) {
        ProgressWindow progressWindow = new ProgressWindow(cancelable, hidable, project);
        progressWindow.setIndeterminate(indeterminate);
        progressWindow.setTitle(title);
        progressWindow.setDelayInMillis(500);
        return progressWindow;
    }

    /**
     * @param jFrame
     * @param width  宽度比例
     * @param height 高度
     */
    public static void sizeWindowOnScreen(JFrame jFrame, float width, float height) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = new Dimension(Math.round(width), Math.round(height));
        int w = (int) ((screenSize.width - frameSize.width) / 2f);
        int h = (int) ((screenSize.height - frameSize.height) / 2f);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setResizable(false);
        jFrame.setLocationRelativeTo(null);
        jFrame.setMinimumSize(frameSize);
        jFrame.setLocation(w, h);
        jFrame.setSize(frameSize);
    }
}
