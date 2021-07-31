package com.fz.plugin;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 文件匹配
 */
public class FileFinder {
    private final String includesPattern;

    private final String exIncludesPattern;

    public FileFinder(String includesPattern) {
        this(includesPattern, "");
    }

    public FileFinder(String includesPattern, String exIncludesPattern) {
        this.includesPattern = includesPattern;
        this.exIncludesPattern = exIncludesPattern;
    }

    public List<String> invoke(File directory) {
        return findIncludeFile(directory);
    }

    public List<String> findIncludeFile(File directory) {
        try {
            DirectoryScanner scanner = createDirScanner(directory);
            String[] files = scanner.getIncludedFiles();
            if (files == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(files);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private DirectoryScanner createDirScanner(File directory) {
        Project project = new Project();
        FileSet fileSet = new FileSet();
        fileSet.setProject(project);
        fileSet.setDir(directory);
        fileSet.setExcludes(this.exIncludesPattern);
        fileSet.setIncludes(this.includesPattern);
        return fileSet.getDirectoryScanner(project);
    }

    public List<String> findIncludeDir(File directory) {
        try {
            DirectoryScanner scanner = createDirScanner(directory);
            String[] files = scanner.getIncludedDirectories();
            if (files == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(files);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
