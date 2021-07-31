package com.fz.plugin.utils;

import com.fz.plugin.bean.ElementBean;
import com.fz.plugin.bean.MultiLanguageBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelUtil {
    /**
     * 只匹配(((?<=（)|(?<=\())[a-z-?A-Z]+((?=）)|(?=\))))匹配()或（）括号之间的字符，
     */
    private static final String LANGUAGE_CODE_REG = "(((?<=（)|(?<=\\())[a-z-?A-Z]+((?=）)|(?=\\))))";
    public static final Pattern LANGUAGE = Pattern.compile(LANGUAGE_CODE_REG);

    /**
     * 匹配小括号之间的字符，如果匹配到，则返回小括号直接的字符作为语言简码，否则返回整个字符串
     *
     * @param content 文本内容
     * @return
     */
    private static String languageCode(String content) {
        Matcher matcher = LANGUAGE.matcher(content);
        if (matcher.find()) {
            return matcher.group();
        }
        return content;
    }

    /**
     * 返回数据结构如下图
     * ┌──────────────────────────────────────────────────────────┐
     * │  │key1   │key2   │key3   │key4   │key5   │key6   │key7   │
     * │en├───────────────────────────────────────────────────────┤
     * │  │value1 │value2 │value3 │value4 │value5 │value6 │value7 │
     * ├──────────────────────────────────────────────────────────┤
     * │  │key8   │key9   │key10  │key11  │key12  │key13  │key14  │
     * │es├───────────────────────────────────────────────────────┤
     * │  │value8 │value9 │value10│value11│value12│value13│value14│
     * ├──────────────────────────────────────────────────────────┤
     * │  │key14  │key15  │key16  │key17  │key18  │key19  │key20  │
     * │fr├───────────────────────────────────────────────────────┤
     * │  │value14│value15│value16│value17│value18│value19│value20│
     * └──────────────────────────────────────────────────────────┘
     *
     * @param sheet
     * @return
     */
    public static Map<String, List<ElementBean>> parseExcelForMap2(Sheet sheet) {
        Map<String, List<ElementBean>> datas = new HashMap<>();
        Row firstRow = null;
        for (Row row : sheet) {
            if (firstRow == null) {
                firstRow = row;
                continue;
            }
            String valueKey = null;
            for (Cell cell : row) {
                if (valueKey == null) {
                    valueKey = ParseUtil.toString(getCellValue(cell));
                    continue;
                }
                //第一行当前列
                final Cell firstRowCurCell = firstRow.getCell(cell.getColumnIndex());
                if (firstRowCurCell != null) {
                    final String languageCode = languageCode(ParseUtil.toString(getCellValue(firstRowCurCell)));
                    final String cellValue = ParseUtil.toString(cellValue(row, cell));
                    if (StringUtils.isNotEmpty(cellValue) && StringUtils.isNotEmpty(languageCode)) {
                        addValue(datas, languageCode, new ElementBean(valueKey, cellValue));
                    }
                }
            }
        }
        return datas;
    }

    private static void addValue(Map<String, List<ElementBean>> datas, String lang, ElementBean bean) {
        if (StringUtils.isNotEmpty(lang)) {
            if (datas.containsKey(lang)) {
                datas.get(lang).add(bean);
            } else {
                datas.put(lang, new ArrayList<>(Collections.singletonList(bean)));
            }
        }
    }

    private static Object cellValue(Row row, Cell cell) {
        CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
        String cellFormat = cellRef.formatAsString();
        Object value = getCellValue(cell);
        System.out.println(MessageFormat.format("{0}-{1}", cellFormat, value));
        return value;
    }

    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        final Object cellValue;
        switch (cell.getCellType()) {
            case STRING:
                cellValue = cell.getRichStringCellValue().getString();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = cell.getDateCellValue();
                } else {
                    cellValue = cell.getNumericCellValue();
                }
                break;
            case BOOLEAN:
                cellValue = cell.getBooleanCellValue();
                break;
            case FORMULA:
                cellValue = cell.getCellFormula();
                break;
            case _NONE:
            case ERROR:
            case BLANK:
            default:
                cellValue = null;
        }
        return cellValue;
    }

    /**
     * 生成Excel 文件
     *
     * @param outputFile
     * @param languages
     */
    public static void generateExcelFile(File outputFile, Map<String, List<MultiLanguageBean>> languages) {
        Map<String, List<MultiLanguageBean>> covertData = convertData(languages);
        HSSFWorkbook work = new HSSFWorkbook();
        HSSFSheet sheet = work.createSheet();
        //固定表头
        sheet.createFreezePane(1, 1);
        sheet.setDefaultColumnWidth(25);
        HSSFCellStyle style = work.createCellStyle();
        style.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.NONE);
        style.setBorderLeft(BorderStyle.NONE);
        style.setBorderRight(BorderStyle.NONE);
        style.setBorderTop(BorderStyle.NONE);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont font = work.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        font.setFontHeightInPoints((short) 20);
        font.setBold(true);
        style.setFont(font);

        HSSFCellStyle style2 = work.createCellStyle();
        style2.setFillForegroundColor(IndexedColors.AUTOMATIC.getIndex());
        style2.setBorderBottom(BorderStyle.NONE);
        style2.setBorderLeft(BorderStyle.NONE);
        style2.setBorderRight(BorderStyle.NONE);
        style2.setBorderTop(BorderStyle.NONE);
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setVerticalAlignment(VerticalAlignment.CENTER);

        HSSFFont font2 = work.createFont();
        font2.setBold(false);
        style2.setFont(font2);
        Iterator<String> keys = covertData.keySet().iterator();
        List<MultiLanguageBean> langs = covertData.get("name");
        langs.sort(Comparator.comparing(MultiLanguageBean::getLanguageCode));
        int row = 0;
        createRow("name", langs, langs, sheet, style, row);
        while (keys.hasNext()) {
            final String key = keys.next();
            ++row;
            createRow(key, langs, covertData.get(key), sheet, style2, row);
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            work.write(outputStream);
            work.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int createRow(String key, List<MultiLanguageBean> langs, List<MultiLanguageBean> list,
                                 HSSFSheet sheet, HSSFCellStyle style, int row) {
        HSSFRow itemRow = sheet.createRow(row);
        HSSFCell itemCell = itemRow.createCell(0);
        itemCell.setCellStyle(style);
        itemCell.setCellValue(key);
        int cell = 1;
        String elementText = "";
        for (MultiLanguageBean lang : langs) {
            elementText = "";
            for (MultiLanguageBean multiLanguageBean : list) {
                if (lang.getLanguageCode().equalsIgnoreCase(multiLanguageBean.getLanguageCode())) {
                    elementText = multiLanguageBean.getValue();
                    break;
                }
            }
            itemCell = itemRow.createCell(cell);
            itemCell.setCellStyle(style);
            itemCell.setCellValue(elementText);
            ++cell;
        }
        return row;
    }

    private static Map<String, List<MultiLanguageBean>> convertData(Map<String, List<MultiLanguageBean>> datas) {
        Map<String, List<MultiLanguageBean>> convertData = new HashMap<>();
        Iterator<String> keys = datas.keySet().iterator();
        while (keys.hasNext()) {
            final String language = keys.next();
            addData(convertData, "name", new MultiLanguageBean(language));
            List<MultiLanguageBean> languageBeans = datas.get(language);
            if (languageBeans != null) {
                for (MultiLanguageBean languageBean : languageBeans) {
                    addData(convertData, languageBean.getName(), new MultiLanguageBean(languageBean));
                }
            }
        }
        return convertData;
    }

    private static void addData(Map<String, List<MultiLanguageBean>> datas, String key, MultiLanguageBean bean) {
        if (StringUtils.isNotEmpty(key)) {
            if (datas.containsKey(key)) {
                datas.get(key).add(bean);
            } else {
                datas.put(key, new ArrayList<>(Collections.singletonList(bean)));
            }
        }
    }
}
