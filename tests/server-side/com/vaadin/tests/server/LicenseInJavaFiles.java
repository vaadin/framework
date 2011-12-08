package com.vaadin.tests.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

public class LicenseInJavaFiles extends TestCase {

    /**
     * The tests are run in the build directory.
     */
    public static String SRC_DIR = "../src";

    public void testJavaFilesContainsLicense() throws IOException {
        File srcDir = new File(SRC_DIR);
        System.out.println(new File(".").getAbsolutePath());
        HashSet<String> missing = new HashSet<String>();
        checkForLicense(srcDir, missing);
        if (!missing.isEmpty()) {
            throw new RuntimeException(
                    "The following files are missing license information:\n"
                            + missing.toString());
        }
    }

    private void checkForLicense(File srcDir, HashSet<String> missing)
            throws IOException {
        Assert.assertTrue("Source directory " + srcDir + " does not exist",
                srcDir.exists());

        for (File f : srcDir.listFiles()) {
            if (f.isDirectory()) {
                checkForLicense(f, missing);
            } else if (f.getName().endsWith(".java")) {
                checkForLicenseInFile(f, missing);
            }
        }
    }

    private void checkForLicenseInFile(File f, HashSet<String> missing)
            throws IOException {
        String contents = IOUtils.toString(new FileInputStream(f));
        if (!contents.contains("@" + "VaadinApache2LicenseForJavaFiles" + "@")) {
            missing.add(f.getPath());
        }

    }
}
