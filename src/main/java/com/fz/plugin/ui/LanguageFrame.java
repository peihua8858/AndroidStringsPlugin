package com.fz.plugin.ui;

import com.fz.plugin.bean.ElementBean;
import com.fz.plugin.bean.TreeModelBean;
import com.fz.plugin.utils.Utils;
import com.fz.plugin.utils.XmlUtil;
import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.diff.DiffManager;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class LanguageFrame extends JFrame {
    private JPanel contentPane;
    private JTree fileTree;
    private JButton buttonOK;
    private JButton buttonCancel;

    public LanguageFrame(Project project, boolean isFormat, TreeNode treeNode, Map<String, List<ElementBean>> datas) {
        super("");
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        fileTree.setModel(new DefaultTreeModel(treeNode));
        fileTree.expandPath(new TreePath(treeNode));
        expandTree(fileTree, new TreePath(treeNode));
        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 如果在这棵树上点击了2次,即双击
                if (e.getSource() == fileTree && e.getClickCount() == 2) {
                    // 按照鼠标点击的坐标点获取路径
                    TreePath selPath = fileTree.getPathForLocation(e.getX(), e.getY());
                    // 谨防空指针异常!双击空白处是会这样
                    if (selPath != null) {
                        // 获取这个路径上的最后一个组件,也就是双击的地方
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                        if (node == null)
                            return;
                        Object object = node.getUserObject();
                        if (object instanceof TreeModelBean) {
                            TreeModelBean modelBean = (TreeModelBean) object;
                            Utils.runWithNotification(() -> {
                                File xmlFile = modelBean.getFile();
                                VirtualFile sFile = LocalFileSystem.getInstance().findFileByIoFile(xmlFile);
                                if (sFile == null) {
                                    Utils.showMessageDialog(project, MessageFormat.format("文件{0}不存在。",
                                            xmlFile.getAbsolutePath()));
                                    return;
                                }
                                final DiffContentFactoryEx myContentFactory = DiffContentFactoryEx.getInstanceEx();
                                List<ElementBean> data = datas.get(modelBean.getLang());
                                String xmlContent = XmlUtil.createXml(modelBean.getFile(), data, isFormat);
                                Utils.invokeLater(() -> {
                                    DiffRequest request = new SimpleDiffRequest("多语言翻译文案对比",
                                            myContentFactory.create(xmlContent, XmlFileType.INSTANCE),
                                            myContentFactory.create(project, sFile), "翻译文案", sFile.getPath());
                                    DiffManager.getInstance().showDiff(project, request);
                                });
                            }, project);
                        }
                    }
                }

            }
        });
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        Utils.sizeWindowOnScreen(this, 700, 360);
        setResizable(true);
        pack();
    }

    /**
     * tree默认节点展开
     *
     * @param tree
     * @param parent
     */

    private static void expandTree(JTree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandTree(tree, path);//展开节点递归
            }
        }
        tree.expandPath(parent);//展开该父节点下面的子节点
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
