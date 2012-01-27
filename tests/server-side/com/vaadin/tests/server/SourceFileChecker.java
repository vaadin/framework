package com.vaadin.tests.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;

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

    public static String getBaseDir() {
        if (baseDirectory != null) {
            return baseDirectory;
        }
        // Run in the "build" directory by build, in the project root by Eclipse
        for (File f : new File("..").listFiles()) {
            if (f.getName().equals("buildhelpers")) {
                // We are in "build"
                baseDirectory = "../";
                return baseDirectory;
            }
        }

        baseDirectory = "";
        return baseDirectory;
    }

    private static final String[] ALL_SRC_DIRS = new String[] { SRC_DIR,
            TESTBENCH_SRC_DIR, SERVERSIDE_SRC_DIR, CLIENTSIDE_SRC_DIR };

    public void testJavaFilesContainsLicense() throws IOException {
        validateJavaFiles(SRC_DIR, new LicenseChecker(),
                "The following files are missing license information:\n{0}");
    }

    public void testJavaFilesUseUnixNewline() throws IOException {
        for (String dir : ALL_SRC_DIRS) {
            validateJavaFiles(dir, new DosNewlineDetector(),
                    "The following files contain CRLF instead of LF:\n{0}");
        }
    }

    public interface FileValidator {
        void validateFile(File f) throws Exception;
    }

    private void validateJavaFiles(String directory, FileValidator validator,
            String errorMessage) {
        File srcDir = new File(directory);
        System.out.println(new File(".").getAbsolutePath());
        HashSet<String> missing = new HashSet<String>();
        validateFiles(srcDir, missing, validator, ".java");
        if (!missing.isEmpty()) {
            throw new RuntimeException(errorMessage.replace("{0}",
                    missing.toString()));
        }

    }

    private void validateFiles(File srcDir, HashSet<String> missing,
            FileValidator validator, String suffix) {
        Assert.assertTrue("Directory " + srcDir + " does not exist",
                srcDir.exists());

        for (File f : srcDir.listFiles()) {
            if (f.isDirectory()) {
                validateFiles(f, missing, validator, suffix);
            } else if (f.getName().endsWith(suffix)) {
                try {
                    validator.validateFile(f);
                } catch (Throwable t) {
                    missing.add(f.getPath());
                }
            }
        }
    }

    class DosNewlineDetector implements FileValidator {

        public void validateFile(File f) throws Exception {
            String contents = IOUtils.toString(new FileInputStream(f));
            if (contents.contains("\r\n")) {
                throw new IllegalArgumentException();
            }

        }
    }

    class LicenseChecker implements FileValidator {
        public void validateFile(File f) throws Exception {
            String contents = IOUtils.toString(new FileInputStream(f));
            if (!contents.contains("@" + "VaadinApache2LicenseForJavaFiles"
                    + "@")) {
                throw new IllegalArgumentException();
            }
        }
    }
}
