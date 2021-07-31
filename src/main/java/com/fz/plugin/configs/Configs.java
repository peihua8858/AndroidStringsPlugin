package com.fz.plugin.configs;

/**
 * 配置工具
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/7/20 11:21
 */
public class Configs {
    /**
     * 项目文件夹
     */
    public static final String DEFAULT_PROJECT_MAIN_FOLDER = "app/src";
    /**
     * 多语言编译前文件夹
     */
    public static final String STRINGS_MAIN_RES_PATH_INCLUDE = "**/main/res/**/values*/strings.xml";
    /**
     * 项目文件夹
     */
    public static final String PROJECT_APP_FOLDER = "app/";
    /**
     * 项目模块文件夹
     */
    public static final String PROJECT_MODULE_SRC_FOLDER = "**/src";
    /**
     * 项目文件夹
     * *.xlsx;*.xls
     */
    public static final String PROJECT_EXCEL_FILE = "*.xlsx,*.xls";
    /**
     * 多语言编译前文件夹
     */
    public static final String STRINGS_MAIN_FOLDER_PATH_INCLUDE = "/main/";
    public static final String STRINGS_MAIN_RES_PATH_EXCLUDES = "**/mipmap*/**,**/menu*/**,**/layout*/**,**/font*/**," +
            "**/drawable*/**,**/color*/**,**/anim*/**,**/raw*/**,**/animator*/**,**/xml*/**";
    public static final String STRINGS_BUILD_PATH_INCLUDE = "**/build/**/values*/*.xml";
}
