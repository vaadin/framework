package com.vaadin.tests.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

public class SourceFileChecker extends TestCase {

    /**
     * The tests are run in the build directory.
     */
    public static String baseDirectory = null;
    public static final String SRC_DIR = getBaseDir() + "src";
    public static final String TESTBENCH_SRC_DIR = getBaseDir()
            + "tests/testbench";
    public static final String SERVERSIDE_SRC_DIR = getBaseDir()
            + "tests/server-side";
    public static final String CLIENTSIDE_SRC_DIR = getBaseDir()
            + "tests/client-side";
    private String externalJavaFiles = "com/vaadin/external";
    private String buildFiles = "build";
    private Set<String> alwaysIgnore = new HashSet<String>();
    {
        alwaysIgnore.add(".settings");
        alwaysIgnore.add("eclipse");
    }

    public static String getBaseDir() {
        if (baseDirectory != null) {
            return baseDirectory;
        }
        // Run in the "build" directory by build, in the project root by Eclipse
        for (File f : new File(".").listFiles()) {
            if (f.getName().equals("buildhelpers")) {
                // We are in "build"
                baseDirectory = "../";
                return baseDirectory;
            }
        }

        baseDirectory = "./";
        return baseDirectory;
    }

    private static final String[] ALL_SRC_DIRS = new String[] { SRC_DIR,
            TESTBENCH_SRC_DIR, SERVERSIDE_SRC_DIR, CLIENTSIDE_SRC_DIR };

    public void testJavaFilesContainsLicense() throws IOException {
        Set<String> ignore = new HashSet<String>(alwaysIgnore);
        ignore.add(externalJavaFiles);
        validateFiles(SRC_DIR, new LicenseChecker(), ignore,
                "The following files are missing license information:\n{0}",
                ".java");
    }

    public void testNonJavaFilesUseUnixNewline() throws IOException {
        Set<String> ignore = new HashSet<String>(alwaysIgnore);
        ignore.add(buildFiles);

        for (String suffix : new String[] { ".html", ".css", ".xml" }) {
            validateFiles(getBaseDir(), new DosNewlineDetector(), ignore,
                    "The following files contain CRLF instead of LF:\n{0}",
                    suffix);
        }
    }

    public void testJavaFilesUseUnixNewline() throws IOException {
        Set<String> ignore = new HashSet<String>(alwaysIgnore);
        ignore.add(externalJavaFiles);
        for (String dir : ALL_SRC_DIRS) {
            validateFiles(dir, new DosNewlineDetector(), ignore,
                    "The following files contain CRLF instead of LF:\n{0}",
                    ".java");
        }
    }

    public void testGwtFilesUsingEntry() {
        Set<String> ignore = new HashSet<String>(alwaysIgnore);
        ignore.add(externalJavaFiles);
        validateFiles(
                SRC_DIR,
                new GwtEntryChecker(),
                ignore,
                "The following files might export javscript callbacks without $entry:\n{0}",
                ".java");
    }

    public interface FileValidator {
        void validateFile(File f) throws Exception;
    }

    private void validateFiles(String directory, FileValidator validator,
            Set<String> ignore, String errorMessage, String ending) {
        File srcDir = new File(directory);
        HashSet<String> missing = new HashSet<String>();
        validateFiles(directory, srcDir, missing, validator, ending, ignore);
        if (!missing.isEmpty()) {
            throw new RuntimeException(errorMessage.replace("{0}", missing
                    .toString().replace(',', '\n')));
        }

    }

    private void validateFiles(String baseDirectory, File directory,
            HashSet<String> missing, FileValidator validator, String suffix,
            Set<String> ignores) {
        Assert.assertTrue("Directory " + directory + " does not exist",
                directory.exists());

        File[] files = directory.listFiles();
        if (files == null) {
            throw new RuntimeException("Listing of directory "
                    + directory.getPath() + " failed");
        }
        for (File f : files) {
            boolean ignoreThis = false;
            for (String ignore : ignores) {
                if (new File(baseDirectory, ignore).equals(f)) {
                    ignoreThis = true;
                    continue;
                }
            }

            if (ignoreThis) {
                continue;
            }

            if (f.isDirectory()) {
                validateFiles(baseDirectory, f, missing, validator, suffix,
                        ignores);
            } else if (f.getName().endsWith(suffix)) {
                try {
                    validator.validateFile(f);
                } catch (Throwable t) {
                    missing.add(f.getPath());
                }
            }
        }
    }

    abstract class FileContentsValidator implements FileValidator {
        public void validateFile(File f) throws Exception {
            FileInputStream fis = new FileInputStream(f);
            String contents = IOUtils.toString(fis);
            fis.close();
            validateContents(f, contents);
        }

        protected abstract void validateContents(File f, String contents)
                throws Exception;

    }

    class DosNewlineDetector extends FileContentsValidator {

        @Override
        protected void validateContents(File f, String contents)
                throws Exception {
            if (contents.contains("\r\n")) {
                throw new IllegalArgumentException();
            }

        }

    }

    class LicenseChecker extends FileContentsValidator {

        @Override
        protected void validateContents(File f, String contents)
                throws Exception {
            if (!contents.contains("@" + "VaadinApache2LicenseForJavaFiles"
                    + "@")) {
                throw new IllegalArgumentException();
            }
        }
    }

    class GwtEntryChecker extends FileContentsValidator {
        // Matches e.g.
        // @com.vaadin.terminal.gwt.client.HistoryImplIEVaadin::escapeHtml(
        private final Matcher matcher = Pattern.compile("@[\\w.]+::\\w+\\(")
                .matcher("");

        @Override
        protected void validateContents(File f, String contents)
                throws Exception {
            matcher.reset(contents);
            while (matcher.find()) {
                int start = matcher.start();

                // Search backwards to find index of native block start
                int nativeBlockStart = contents.lastIndexOf("/*-{", start);

                // Get contents between block start and our match
                String beforeMatchInBlock = contents.substring(
                        nativeBlockStart, start);

                // Fail if there's no $entry
                if (!beforeMatchInBlock.contains("$entry")) {
                    throw new IllegalArgumentException();
                }
            }
        }

    }
}
