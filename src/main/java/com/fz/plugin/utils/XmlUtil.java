package com.fz.plugin.utils;

import com.fz.plugin.FileFinder;
import com.fz.plugin.bean.ElementBean;
import com.fz.plugin.bean.MultiLanguageBean;
import com.fz.plugin.bean.MutableTreeNode;
import com.fz.plugin.bean.TreeModelBean;
import com.fz.plugin.configs.Configs;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.*;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class XmlUtil {
    private static final ThreadLocal<SimpleDateFormat> DEFAULT_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
    };

    private static String formatDate() {
        return DEFAULT_DATE_FORMAT.get().format(new Date());
    }

    private static boolean checkFile(File remoteFile, String lang) {
        if (remoteFile == null) {
            return false;
        }
        final File parentFile = remoteFile.getParentFile();
        return checkFileName(parentFile.getName(), lang);
    }

    private static boolean checkFileName(String fileName, String lang) {
        final String valueName = createValuesFile(lang);
        return valueName.equalsIgnoreCase(fileName) || (("en".equalsIgnoreCase(lang) || "default".equalsIgnoreCase(lang)) && "values".equalsIgnoreCase(fileName));
    }

    private static String createValuesFile(String lang) {
        return "values-" + checkLanguage(lang);
    }

    static String checkLanguage(String lang) {
        if (lang == null || "default".equalsIgnoreCase(lang)) {
            return "en";
        }
        return lang;
    }

    /**
     * 强制替换xml 数据
     *
     * @param datas excel 表格数据
     * @author dingpeihua
     * @date 2019/7/30 18:57
     * @version 1.0
     */
    public static void forceReplace(Map<String, List<ElementBean>> datas, boolean isFormat, File rootDir) {
        List<String> mainFolderFileNames = FileUtils.scanMainResFiles(rootDir);
        if (!mainFolderFileNames.isEmpty()) {
            forceReplace(datas, isFormat, mainFolderFileNames, rootDir);
        }
        String rootDirPath = rootDir.getAbsolutePath();
        //剩余数据是没有匹配的文件，需要新创建文件
        final File parentFile;
        if (rootDirPath.contains("/main")) {
            parentFile = new File(rootDir, "/res/");
        } else {
            parentFile = new File(rootDir, "/main/res/");
        }
        Iterator<Map.Entry<String, List<ElementBean>>> iterator = datas.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, List<ElementBean>> entry = iterator.next();
            final String lang = entry.getKey();
            final String valueName = createValuesFile(lang);
            final File valuesFile = new File(parentFile, valueName);
            if (!valuesFile.exists()) {
                valuesFile.mkdirs();
            }
            final File remoteFile = new File(valuesFile, "strings.xml");
            forceChangeXmlDataByDom(remoteFile, isFormat, lang, datas.get(lang));
        }
    }

    static void forceReplace(Map<String, List<ElementBean>> datas, boolean isFormat, List<String> fileNames,
                             File rootDir) {
        Iterator<Map.Entry<String, List<ElementBean>>> iterator = datas.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, List<ElementBean>> entry = iterator.next();
            final String lang = entry.getKey();
            for (String fileName : fileNames) {
                final File remoteFile = new File(rootDir, fileName);
                if (checkFile(remoteFile, lang)) {
                    forceChangeXmlDataByDom(remoteFile, isFormat, lang, datas.get(lang));
                    //写入文件之后删除数据
                    iterator.remove();
                    break;
                }
            }
        }
    }

    public static void forceChangeXmlDataByDom(File remoteFile, boolean isFormat, String language,
                                               List<ElementBean> data) {
        createXml(remoteFile, null, isFormat, data);
    }


    public static TreeNode createTreeNode(File rootDir, Map<String, List<ElementBean>> excelDatas) {
        List<String> mainFolderFileNames = FileUtils.scanMainResFiles(rootDir);
        Map<String, List<ElementBean>> datas = deepClone((HashMap<String, List<ElementBean>>) excelDatas);
        Iterator<Map.Entry<String, List<ElementBean>>> iterator = datas.entrySet().iterator();
        ArrayList<MutableTreeNode> treeNodes = new ArrayList<>();
        while (iterator.hasNext()) {
            final Map.Entry<String, List<ElementBean>> entry = iterator.next();
            final String lang = entry.getKey();
            for (String fileName : mainFolderFileNames) {
                final File remoteFile = new File(rootDir, fileName);
                final File parentFile = remoteFile.getParentFile();
                final String parentFileName = parentFile.getName();
                if (checkFileName(parentFileName, lang)) {
//                    final MutableTreeNode childNode = new MutableTreeNode(parentFileName);
                    TreeModelBean model = new TreeModelBean();
                    model.setFile(remoteFile);
                    model.setFileName(remoteFile.getName());
                    model.setLang(lang);
//                    childNode.add(new MutableTreeNode(model));
                    treeNodes.add(addChildNode(parentFileName, model));
//                    treeNodes.add(childNode);
                    //写入文件之后删除数据
                    iterator.remove();
                    break;
                }
            }
        }
        String rootDirPath = rootDir.getAbsolutePath();
        //剩余数据是没有匹配的文件，需要新创建文件
        final File parentFile;
        if (rootDirPath.contains("/main")) {
            parentFile = new File(rootDir, "/res/");
        } else {
            parentFile = new File(rootDir, "/main/res/");
        }
        Iterator<String> langs = datas.keySet().iterator();
        while (langs.hasNext()) {
            final String lang = langs.next();
            final String valueName = createValuesFile(lang);
            final File valuesFile = new File(parentFile, valueName);
            if (!valuesFile.exists()) {
                valuesFile.mkdirs();
            }
            final File remoteFile = new File(valuesFile, "strings.xml");
            try {
                remoteFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            final MutableTreeNode childNode = new MutableTreeNode(valueName);
            TreeModelBean model = new TreeModelBean();
            model.setFile(remoteFile);
            model.setFileName(remoteFile.getName());
            model.setLang(lang);
//            childNode.add(new MutableTreeNode(model));
            treeNodes.add(addChildNode(valueName, model));
        }
        treeNodes.sort(MutableTreeNode::compareTo);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("res");
        for (MutableTreeNode treeNode : treeNodes) {
            root.add(treeNode);
        }
        return root;
    }

    private static MutableTreeNode addChildNode(String valueName, Object model) {
        final MutableTreeNode childNode = new MutableTreeNode(valueName);
        childNode.add(new MutableTreeNode(model));
        return childNode;
    }

    public static <T extends Serializable> T deepClone(T o) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    public static String createXml(File remoteFile, List<ElementBean> datas, boolean isFormat) {
        if (datas == null || datas.isEmpty()) {
            return "";
        }
        StringWriter writer = new StringWriter();
        createXml(remoteFile, writer, isFormat, datas);
        return writer.toString();
    }

    public static void createXml(File remoteFile, Writer writer, boolean isFormat, List<ElementBean> data) {
        try {
            if (data == null || data.isEmpty()) {
                return;
            }
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document document;
            Element resourceElementRoot;
            //判断文件存在，并且不是空文件
            if (remoteFile != null && remoteFile.exists() && remoteFile.length() > 0) {
                InputStream inputStream = new FileInputStream(remoteFile);
                Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                InputSource is = new InputSource(reader);
                is.setEncoding("UTF-8");
                document = docBuilder.parse(is);
            } else {
                document = docBuilder.newDocument();
            }
            resourceElementRoot = document.getDocumentElement();
            if (resourceElementRoot == null) {
                //<resources tools:ignore="MissingTranslation" xmlns:tools="http://schemas.android.com/tools">
                resourceElementRoot = document.createElement("resources");
                Attr attrXmlns = document.createAttribute("xmlns:tools");
                attrXmlns.setValue("http://schemas.android.com/tools");
                resourceElementRoot.setAttributeNode(attrXmlns);
                Attr attrTools = document.createAttribute("tools:ignore");
                attrTools.setValue("MissingTranslation");
                resourceElementRoot.setAttributeNode(attrTools);
                document.appendChild(resourceElementRoot);
            }
            //<!--2020-04-10--start-->
            boolean addComment = false;
            NodeList elements = resourceElementRoot.getElementsByTagName("string");
            int length = elements != null ? elements.getLength() : 0;
            boolean hasElements = length > 0;
            boolean isAdd;
            for (ElementBean datum : data) {
                String key = datum.getKey();
                isAdd = true;
                if (hasElements) {
                    for (int index = 0; index < length; index++) {
                        Node nNode = elements.item(index);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            Attr attr = eElement.getAttributeNode("name");
                            if (key.equalsIgnoreCase(attr.getValue())) {
                                isAdd = false;
                                String value = datum.getValue();
                                if (checkSpecialCharacters(value)) {
                                    if (eElement.hasChildNodes()) {
                                        removeAllChild(eElement);
                                    } else {
                                        eElement.setTextContent("");
                                    }
                                    eElement.appendChild(document.createCDATASection(value));
                                } else {
                                    eElement.setTextContent(value);
                                }
                                break;
                            }
                        }
                    }
                }
                if (isAdd) {
                    if (!addComment) {
                        addComment = true;
                        Comment startComment = document.createComment(MessageFormat.format("{0} >> start",
                                formatDate()));
                        resourceElementRoot.appendChild(startComment);
                    }
                    Element entry = document.createElement("string");
                    Attr attr = document.createAttribute("name");
                    attr.setValue(datum.getKey());
                    entry.setAttributeNode(attr);
                    String value = datum.getValue();
                    if (checkSpecialCharacters(value)) {
                        entry.appendChild(document.createCDATASection(value));
                    } else {
                        entry.setTextContent(value);
                    }
                    resourceElementRoot.appendChild(entry);
                }
            }
            if (addComment) {
                Comment endComment = document.createComment(MessageFormat.format("{0} >> end", formatDate()));
                resourceElementRoot.appendChild(endComment);
            }
            if (writer == null && remoteFile != null) {
                writer = new OutputStreamWriter(new FileOutputStream(remoteFile), StandardCharsets.UTF_8);
            }
            transformer(document, isFormat, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查是否有html <></>标签
     * 防止 <font></font>标签被转义，导致android strings处理失败
     *
     * @author dingpeihua
     * @date 2020/6/8 18:19
     * @version 1.0
     */
    private static boolean checkSpecialCharacters(String value) {
        return value.contains("<") || value.contains(">");
    }

    private static void removeAllChild(Element e) {
        NodeList elements = e.getChildNodes();
        for (int i = 0; i < elements.getLength(); ) {
            final Node nNode = elements.item(i);
            e.removeChild(nNode);
        }
    }

    private static void transformer(Document document, boolean isFormat, Writer writer) {
        try {
            if (isFormat) {
                DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
                DOMImplementationLS impls = (DOMImplementationLS) registry.getDOMImplementation("LS");
                //Prepare the output
                LSOutput domOutput = impls.createLSOutput();
                domOutput.setCharacterStream(writer);
                domOutput.setEncoding("UTF-8");
                //Prepare the serializer
                LSSerializer domWriter = impls.createLSSerializer();
                DOMConfiguration domConfig = domWriter.getDomConfig();
                domConfig.setParameter("format-pretty-print", true);
                domConfig.setParameter("element-content-whitespace", true);
                domWriter.setNewLine("\r\n");
                domConfig.setParameter("cdata-sections", Boolean.TRUE);
                //And finaly, write
                domWriter.write(document, domOutput);
            } else {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(document);
                StreamResult consoleResult = new StreamResult(writer);
                transformer.transform(source, consoleResult);
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, List<MultiLanguageBean>> paresXmlMultiLanguage(File rootDir, boolean isOnlyMainFolder) {
        Map<String, List<MultiLanguageBean>> datas = new HashMap<>();
        try {
            String mainFolderPattern = "**/" + Configs.STRINGS_MAIN_RES_PATH_INCLUDE;
            List<String> mainFolderFileNames = FileUtils.scanFiles(rootDir, mainFolderPattern);
            if (isOnlyMainFolder) {
                for (String fileName : mainFolderFileNames) {
                    final File remoteFile = new File(rootDir, fileName);
                    final File parentFile = remoteFile.getParentFile();
                    final String parentFileName = parentFile.getName();
                    final String languageCode = splitLanguage(parentFileName);
                    datas.put(languageCode, createMultiLanguage(languageCode, remoteFile));
                }
            } else {
                String buildFolderPattern = "**/" + Configs.STRINGS_BUILD_PATH_INCLUDE;
                List<String> buildFolderFileNames = FileUtils.scanFiles(rootDir, buildFolderPattern);
                for (String buildFolderFileName : buildFolderFileNames) {
                    final File buildRemoteFile = new File(rootDir, buildFolderFileName);
                    final String buildFileName = buildRemoteFile.getName();
                    for (String fileName : mainFolderFileNames) {
                        final File remoteFile = new File(rootDir, fileName);
                        final File parentFile = remoteFile.getParentFile();
                        final String parentFileName = parentFile.getName();
                        if (buildFileName.equalsIgnoreCase(parentFileName + ".xml")) {
                            String languageCode = splitLanguage(parentFileName);
                            datas.put(languageCode, createMultiLanguage(languageCode, buildRemoteFile));
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }

    private static List<MultiLanguageBean> createMultiLanguage(String languageCode, File remoteFile) {
        List<MultiLanguageBean> multiLanguageBeans = new ArrayList<>();
        try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document document = docBuilder.parse(remoteFile);
            Element elementRoot = document.getDocumentElement();
            NodeList elements = elementRoot.getElementsByTagName("string");
            int length = elements != null ? elements.getLength() : 0;
            boolean hasElements = length > 0;
            if (hasElements) {
                for (int index = 0; index < length; index++) {
                    Node nNode = elements.item(index);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) nNode;
                        final boolean hasTranslatable = element.hasAttribute("translatable");
                        final String translatable = element.getAttribute("translatable");
                        //只有当translatable ==false时才抛弃
                        if (translatable == null || !hasTranslatable || ParseUtil.toBoolean(translatable)) {
                            final String name = element.getAttribute("name");
                            final String text = Utils.removeDoubleQuotes(element.getTextContent());
                            if (!StringUtils.isEmpty(text)) {
                                final MultiLanguageBean bean = new MultiLanguageBean();
                                bean.setLanguage("");
                                bean.setLanguageCode(languageCode);
                                bean.setName(name);
                                bean.setValue(text);
                                multiLanguageBeans.add(bean);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return multiLanguageBeans;
    }

    static String splitLanguage(String valuesName) {
        if (valuesName == null || !valuesName.contains("-")) {
            return "en";
        }
        int index = valuesName.indexOf("-");
        if (index + 1 >= valuesName.length()) {
            return "en";
        }
        return valuesName.substring(index + 1);
    }
}
