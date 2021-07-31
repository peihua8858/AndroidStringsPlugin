package com.fz.plugin.ui;

import com.fz.plugin.bean.ComboBoxListModel;
import com.fz.plugin.bean.ComboBoxModelBean;
import com.fz.plugin.bean.ElementBean;
import com.fz.plugin.bean.MultiLanguageBean;
import com.fz.plugin.configs.Configs;
import com.fz.plugin.utils.ExcelUtil;
import com.fz.plugin.utils.FileUtils;
import com.fz.plugin.utils.Utils;
import com.fz.plugin.utils.XmlUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ToolsFrame extends JFrame {
    private static final Logger LOG = Logger.getInstance(ToolsFrame.class);
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTabbedPane tabPane;
    private JButton btnBrowser;
    private JCheckBox jcbExportLocalFile;
    private JTextField jtfExportSaveFilePath;
    private JButton btnExportSaveFile;
    private JTextField jtWorksheetName;
    private JCheckBox cbForceReplace;
    private JCheckBox jcFormatXml;
    private JComboBox<ComboBoxModelBean> cbExcelFilePath;
    private JComboBox<ComboBoxModelBean> cbMainFolder;
    private JButton btnSelectModule;
    private JButton btnSelectExportModuleFolder;
    private JComboBox<ComboBoxModelBean> cbExportModuleFolder;
    private JCheckBox cbContainLib;
    private JFileChooser mOpenFileDialog;
    private JFileChooser mSaveFileDialog;
    private JFileChooser mSelectModuleFileDialog;
    private JFileChooser mSelectExportModuleFileDialog;
    private int selectedPanelIndex;
    private final Project project;
    private final File rootDir;
    private String excelFilePath;
    private String moduleFolderPath;
    private String exportModuleFolderPath;


    public ToolsFrame(File rootDir, Project project, List<String> excelFiles,
                      List<String> moduleFiles) throws HeadlessException {
        super("");
        setContentPane(contentPane);

        getRootPane().setDefaultButton(buttonOK);
        this.project = project;
        this.rootDir = rootDir;
        btnBrowser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mOpenFileDialog == null) {
                    mOpenFileDialog = new JFileChooser();
                }
                FileNameExtensionFilter xlsx = new FileNameExtensionFilter("MS Excel file(*.xlsx;*.xls)",
                        "xlsx", "xls");
                mOpenFileDialog.setCurrentDirectory(rootDir);
                mOpenFileDialog.addChoosableFileFilter(xlsx);
                mOpenFileDialog.setAcceptAllFileFilterUsed(false);
                mOpenFileDialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                if (mOpenFileDialog.showOpenDialog(ToolsFrame.this) == JFileChooser.APPROVE_OPTION) {
                    String path = mOpenFileDialog.getSelectedFile().getAbsolutePath();
                    if (!path.endsWith(".xlsx") && !path.endsWith(".xls")) {
                        JOptionPane.showMessageDialog(ToolsFrame.this,
                                "输入的文件类型不合法！输入文件必须是xlsx文件！");
                        return;
                    }
                    addComBoxModel(path);
                }
            }
        });
        btnSelectModule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mSelectModuleFileDialog == null) {
                    mSelectModuleFileDialog = new JFileChooser();
                }
                mSelectModuleFileDialog.setCurrentDirectory(rootDir);
                mSelectModuleFileDialog.setAcceptAllFileFilterUsed(false);
                mSelectModuleFileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (mSelectModuleFileDialog.showOpenDialog(ToolsFrame.this) == JFileChooser.APPROVE_OPTION) {
                    String path = mSelectModuleFileDialog.getSelectedFile().getAbsolutePath();
                    addModuleComBoxModel(path);
                }
            }
        });
        btnSelectExportModuleFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mSelectExportModuleFileDialog == null) {
                    mSelectExportModuleFileDialog = new JFileChooser();
                }
                mSelectExportModuleFileDialog.setCurrentDirectory(rootDir);
                mSelectExportModuleFileDialog.setAcceptAllFileFilterUsed(false);
                mSelectExportModuleFileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (mSelectExportModuleFileDialog.showOpenDialog(ToolsFrame.this) == JFileChooser.APPROVE_OPTION) {
                    String path = mSelectExportModuleFileDialog.getSelectedFile().getAbsolutePath();
                    addExportModuleComBoxModel(path);
                }
            }
        });
        PromptSupport.init("您要导入的数据在哪个工作表？如：Sheet1", Color.LIGHT_GRAY,
                null, jtWorksheetName);
        tabPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                selectedPanelIndex = tabPane.getSelectedIndex();
            }
        });
        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (selectedPanelIndex) {
                    case 0:
                        parseExcelToXml();
                        break;
                    case 1:
                        parseStringXmlToExcel();
                        break;
                }
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        btnExportSaveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mSaveFileDialog == null) {
                    mSaveFileDialog = new JFileChooser();
                }
                FileNameExtensionFilter xlsx = new FileNameExtensionFilter("MS Excel file(*.xlsx;*.xls)",
                        "xlsx", "xls");
                mSaveFileDialog.addChoosableFileFilter(xlsx);
                mSaveFileDialog.setAcceptAllFileFilterUsed(false);
                mSaveFileDialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                if (mSaveFileDialog.showSaveDialog(ToolsFrame.this) == JFileChooser.APPROVE_OPTION) {
                    String path = mSaveFileDialog.getSelectedFile().getAbsolutePath();
                    jtfExportSaveFilePath.setText(path);
                }
            }
        });
        if (!excelFiles.isEmpty()) {
            ComboBoxListModel myModel = new ComboBoxListModel();
            for (String excelFile : excelFiles) {
                final File file = new File(rootDir, excelFile);
                myModel.addElement(new ComboBoxModelBean(file.getAbsolutePath(), file.getName()));
            }
            cbExcelFilePath.setModel(myModel);
            setSelectExcelFilePath(new ItemEvent(cbExcelFilePath, ItemEvent.ITEM_STATE_CHANGED,
                    null, ItemEvent.SELECTED));
        }
        if (!moduleFiles.isEmpty()) {
            ComboBoxListModel myModel = new ComboBoxListModel();
            ComboBoxListModel exportModel = new ComboBoxListModel();
            for (String moduleFile : moduleFiles) {
                final File file = new File(rootDir, moduleFile);
                final File parentFile = file.getParentFile();
                myModel.addElement(new ComboBoxModelBean(file.getAbsolutePath(), parentFile.getName()));
                exportModel.addElement(new ComboBoxModelBean(parentFile.getAbsolutePath(), parentFile.getName()));
            }
            cbMainFolder.setModel(myModel);
            cbExportModuleFolder.setModel(exportModel);
            setSelectModuleFilePath(new ItemEvent(cbMainFolder, ItemEvent.ITEM_STATE_CHANGED,
                    null, ItemEvent.SELECTED));
            setSelectExcelModuleFilePath(new ItemEvent(cbExportModuleFolder, ItemEvent.ITEM_STATE_CHANGED,
                    null, ItemEvent.SELECTED));
        }
        cbExcelFilePath.addItemListener(this::setSelectExcelFilePath);
        cbMainFolder.addItemListener(this::setSelectModuleFilePath);
        cbExportModuleFolder.addItemListener(this::setSelectExcelModuleFilePath);
        Utils.sizeWindowOnScreen(this, 550, 260);
        pack();
    }

    private void setSelectExcelFilePath(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            ComboBoxModelBean selectedBook = (ComboBoxModelBean) cbExcelFilePath.getSelectedItem();
            if (selectedBook != null) {
                excelFilePath = selectedBook.getOriFilePath();
                System.out.println(MessageFormat.format("{0}:{1}", selectedBook.getShowPath(),
                        selectedBook.getOriFilePath()));
            }
        }
    }

    private void setSelectModuleFilePath(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            ComboBoxModelBean selectedBook = (ComboBoxModelBean) cbMainFolder.getSelectedItem();
            if (selectedBook != null) {
                moduleFolderPath = selectedBook.getOriFilePath();
                System.out.println(MessageFormat.format("{0}:{1}", selectedBook.getShowPath(),
                        selectedBook.getOriFilePath()));
            }
        }
    }

    private void setSelectExcelModuleFilePath(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            ComboBoxModelBean selectedBook = (ComboBoxModelBean) cbExportModuleFolder.getSelectedItem();
            if (selectedBook != null) {
                exportModuleFolderPath = selectedBook.getOriFilePath();
                System.out.println(MessageFormat.format("{0}:{1}", selectedBook.getShowPath(),
                        selectedBook.getOriFilePath()));
            }
        }
    }

    public void addComBoxModel(String filePath) {
        final File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        ComboBoxModel<ComboBoxModelBean> comboBoxModel = cbExcelFilePath.getModel();
        ComboBoxListModel model;
        if (comboBoxModel instanceof ComboBoxListModel) {
            model = (ComboBoxListModel) comboBoxModel;
        } else {
            model = new ComboBoxListModel();
        }
        int length = model.getSize();
        boolean isAdd = true;
        for (int i = 0; i < length; i++) {
            final ComboBoxModelBean modelBean = model.getElementAt(i);
            if (filePath.equalsIgnoreCase(modelBean.getOriFilePath())) {
                cbExcelFilePath.setSelectedIndex(i);
                isAdd = false;
                break;
            }
        }
        excelFilePath = filePath;
        if (isAdd) {
            ComboBoxModelBean modelBean = new ComboBoxModelBean(file.getAbsolutePath(), file.getName());
            model.addElement(modelBean);
            cbExcelFilePath.setModel(model);
            cbExcelFilePath.setSelectedItem(modelBean);
        }
    }

    public void addModuleComBoxModel(String filePath) {
        final File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        ComboBoxModel<ComboBoxModelBean> comboBoxModel = cbMainFolder.getModel();
        ComboBoxListModel model;
        if (comboBoxModel instanceof ComboBoxListModel) {
            model = (ComboBoxListModel) comboBoxModel;
        } else {
            model = new ComboBoxListModel();
        }
        int length = model.getSize();
        boolean isAdd = true;
        for (int i = 0; i < length; i++) {
            final ComboBoxModelBean modelBean = model.getElementAt(i);
            File oriFile = new File(modelBean.getOriFilePath());
            if (filePath.equalsIgnoreCase(oriFile.getAbsolutePath())
                    || filePath.equalsIgnoreCase(oriFile.getParentFile().getAbsolutePath())) {
                cbMainFolder.setSelectedIndex(i);
                isAdd = false;
                break;
            }
        }
        moduleFolderPath = filePath;
        if (isAdd) {
            ComboBoxModelBean modelBean = new ComboBoxModelBean(file.getAbsolutePath(), file.getName());
            model.addElement(modelBean);
            cbMainFolder.setModel(model);
            cbMainFolder.setSelectedItem(modelBean);
        }
    }

    public void addExportModuleComBoxModel(String filePath) {
        final File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        ComboBoxModel<ComboBoxModelBean> comboBoxModel = cbExportModuleFolder.getModel();
        ComboBoxListModel model;
        if (comboBoxModel instanceof ComboBoxListModel) {
            model = (ComboBoxListModel) comboBoxModel;
        } else {
            model = new ComboBoxListModel();
        }
        int length = model.getSize();
        boolean isAdd = true;
        for (int i = 0; i < length; i++) {
            final ComboBoxModelBean modelBean = model.getElementAt(i);
            File oriFile = new File(modelBean.getOriFilePath());
            if (filePath.equalsIgnoreCase(oriFile.getAbsolutePath())
                    || filePath.equalsIgnoreCase(oriFile.getParentFile().getAbsolutePath())) {
                cbExportModuleFolder.setSelectedIndex(i);
                isAdd = false;
                break;
            }
        }
        exportModuleFolderPath = filePath;
        if (isAdd) {
            ComboBoxModelBean modelBean = new ComboBoxModelBean(file.getAbsolutePath(), file.getName());
            model.addElement(modelBean);
            cbExportModuleFolder.setModel(model);
            cbExportModuleFolder.setSelectedItem(modelBean);
        }
    }

    /**
     * 扫描解析values.xml，并上传到服务器
     *
     * @author dingpeihua
     * @date 2019/7/29 14:52
     * @version 1.0
     */
    private void parseStringXmlToExcel() {
        String saveFilePath = this.jtfExportSaveFilePath.getText();
        LOG.info("ToolsSettings>>" + saveFilePath);
        if (StringUtils.isEmpty(saveFilePath)) {
            showMessageDialog("存储文件路径不能为空！");
            return;
        }
        File file = new File(saveFilePath);
        if (file.isDirectory()) {
            String dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());
            file = new File(saveFilePath, project.getName() + "-" + dateFormat + ".xls");
        }
        if (!jcbExportLocalFile.isSelected()) {
            return;
        }
        boolean containLib = cbContainLib.isSelected();
        File outputFile = file;
        Utils.runWithNotification(() -> {
            File moduleFile;
            if (StringUtils.isNotEmpty(exportModuleFolderPath)) {
                moduleFile = new File(exportModuleFolderPath);
            } else {
                moduleFile = new File(rootDir, Configs.PROJECT_APP_FOLDER);
            }
            Map<String, List<MultiLanguageBean>> languages = XmlUtil.paresXmlMultiLanguage(moduleFile, !containLib);
            ExcelUtil.generateExcelFile(outputFile, languages);
            showMessageDialog("生成Excel文件成功！");
        }, project);
    }

    /**
     * 解析Excel文件并生成strings.xml
     *
     * @author dingpeihua
     * @date 2019/7/29 14:53
     * @version 1.0
     */
    private void parseExcelToXml() {
        LOG.info("ToolsSettings>>" + excelFilePath);
        if (StringUtils.isEmpty(excelFilePath)) {
            showMessageDialog("文件路径不能为空！");
            return;
        }
        File excelFile = new File(excelFilePath);
        if (!excelFile.exists()) {
            showMessageDialog("文件路径" + excelFilePath + "不存在！");
            return;
        }
        String extension = FilenameUtils.getExtension(excelFile.getName());
        LOG.info("ToolsSettings>>extension:" + extension);
        System.out.println("ToolsSettings>>extension:" + extension);
        Workbook wb = null;
        //根据文件后缀（xls/xlsx）进行判断
        try {
            if ("xls".equals(extension)) {
                wb = new HSSFWorkbook(new FileInputStream(excelFile));
            } else if ("xlsx".equals(extension)) {
                wb = new XSSFWorkbook(excelFile);
            } else {
                showMessageDialog("文件类型错误");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessageDialog("出现错误:" + e.getMessage());
            return;
        }
        String sheet1 = jtWorksheetName.getText();
        LOG.info("ToolsSettings>>sheet1" + sheet1);
        if (StringUtils.isEmpty(sheet1)) {
            sheet1 = "Sheet1";
        }
        Workbook finalWb = wb;
        String finalSheet = sheet1;
        boolean isForceReplace = cbForceReplace.isSelected();
        buttonOK.setEnabled(false);
        buttonCancel.setEnabled(false);
        ProgressWindow window = Utils.makeProgress("处理中，请稍后...",
                project, true, false, false);
        Utils.runWithNotification(() -> {
            Sheet sheet = finalWb.getSheet(finalSheet);
            Map<String, List<ElementBean>> excelDatas = ExcelUtil.parseExcelForMap2(sheet);
            boolean isFormat = jcFormatXml.isSelected();
            File moduleFile;
            if (StringUtils.isNotEmpty(moduleFolderPath)) {
                moduleFile = new File(moduleFolderPath);
            } else {
                moduleFile = new File(rootDir, Configs.DEFAULT_PROJECT_MAIN_FOLDER);
            }
            if (isForceReplace) {
                //强制替换，不显示对比列表
                XmlUtil.forceReplace(excelDatas, isFormat, moduleFile);
                showMessageDialog("处理完成！");
            } else {
                TreeNode treeNode = XmlUtil.createTreeNode(moduleFile, excelDatas);
                Utils.invokeLater(() -> {
                    LanguageFrame languageFrame = new LanguageFrame(project, isFormat, treeNode, excelDatas);
                    languageFrame.setVisible(true);
                });
            }
            enabledButton();
        }, project, window);
        window.addStateDelegate(new ProgressIndicatorBase());
    }

    private void showMessageDialog(String message) {
        Utils.showMessageDialog(project, message);
    }

    private void enabledButton() {
        Utils.invokeLater(() -> {
            buttonOK.setEnabled(true);
            buttonCancel.setEnabled(true);
        });
    }

    public static void main(String[] args) {
        File rootDir = new File("");
        List<String> excelFiles = FileUtils.scanFiles(rootDir, Configs.PROJECT_EXCEL_FILE);
        List<String> moduleFiles = FileUtils.scanFolder(rootDir, Configs.PROJECT_MODULE_SRC_FOLDER);
        ToolsFrame dialog = new ToolsFrame(rootDir, null,
                excelFiles, moduleFiles);
        dialog.pack();
        dialog.setVisible(true);
    }
}
