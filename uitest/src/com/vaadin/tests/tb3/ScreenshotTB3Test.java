/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * 
 */
package com.vaadin.tests.tb3;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.commands.TestBenchCommands;

public abstract class ScreenshotTB3Test extends AbstractTB3Test {

    private List<String> screenshotFailures = new ArrayList<String>();;

    /**
     * @since
     * @param identifier
     * @throws IOException
     */
    protected void compareScreen(String identifier) throws IOException {
        if (identifier == null || identifier.isEmpty()) {
            throw new IllegalArgumentException("Empty identifier not supported");
        }

        Parameters.setScreenshotErrorDirectory(getScreenshotErrorDirectory());
        Parameters
                .setScreenshotReferenceDirectory(getScreenshotReferenceDirectory());
        File ref = getScreenshotReferenceFile(getTestName(),
                getDesiredCapabilities(), identifier);

        List<File> alternativeFiles = findReferenceAlternatives(ref);
        List<File> failedAlternatives = new ArrayList<File>();

        for (File file : alternativeFiles) {
            if (testBench(driver).compareScreen(file)) {
                break;
            } else {
                failedAlternatives.add(file);
                if (file != ref) {
                    // Remove alternative reference image since it's the same as
                    // the original reference
                    getFailureFile(file).delete();
                }
            }
        }

        if (failedAlternatives.size() < alternativeFiles.size()) {
            // Success with one of the alternatives, remove files produced by
            // alternatives that already failed
            for (File failedAlternative : failedAlternatives) {
                File failurePng = getFailureFile(failedAlternative);
                if (failedAlternative == ref) {
                    // Image not deleted for original alternative
                    failurePng.delete();
                }
                // Html comparison for all failed comparisons
                new File(failurePng.getParentFile(), failurePng.getName()
                        .replace(".png", ".html")).delete();
            }
        } else {
            screenshotFailures.add(identifier);
        }
    }

    private File getFailureFile(File referenceFile) {
        return new File(referenceFile.getAbsolutePath().replace(
                getScreenshotReferenceDirectory(),
                getScreenshotErrorDirectory()));
    }

    private List<File> findReferenceAlternatives(File reference) {
        List<File> files = new ArrayList<File>();
        files.add(reference);

        File screenshotDir = reference.getParentFile();
        String name = reference.getName();
        // Remove ".png"
        String nameBase = name.substring(0, name.length() - 4);
        for (int i = 1;; i++) {
            File file = new File(screenshotDir, nameBase + "_" + i + ".png");
            if (file.exists()) {
                files.add(file);
            } else {
                break;
            }
        }

        return files;
    }

    private String getScreenshotErrorDirectory() {
        return getScreenshotDirectory() + "/errors";
    }

    private File getScreenshotReferenceFile(String testName,
            DesiredCapabilities capabilities, String identifier) {
        String browserIdentifier = getBrowserIdentifier(capabilities);
        String platform = getPlatform(capabilities);
        String browserVersion = capabilities.getVersion();

        // WindowMaximizeRestoreTest_Windows_InternetExplorer_8_window-1-moved-maximized-restored.png
        String nameStart = getScreenshotReferenceDirectory() + "/" + testName
                + "_" + platform + "_" + browserIdentifier + "_";
        String nameEnd = "_" + identifier + ".png";

        File originalName = new File(nameStart + browserVersion + nameEnd);
        if (originalName.exists()) {
            return originalName;
        } else if (browserVersion.matches("\\d+")) {
            for (int version = Integer.parseInt(browserVersion); version > 0; version--) {
                File file = new File(nameStart + version + nameEnd);
                if (file.exists()) {
                    return file;
                }
            }
        }

        return originalName;
    }

    /**
     * @since
     * @return
     */
    protected String getScreenshotReferenceDirectory() {
        return getScreenshotDirectory() + "/reference";
    }

    /**
     * The screenshot directory containing references and errors
     * 
     * @since
     * @return
     */
    protected abstract String getScreenshotDirectory();

    /**
     * @since
     * @param capabilities
     * @return
     */
    public static String getPlatform(DesiredCapabilities capabilities) {
        if (capabilities.getPlatform() == Platform.WIN8
                || capabilities.getPlatform() == Platform.WINDOWS
                || capabilities.getPlatform() == Platform.VISTA
                || capabilities.getPlatform() == Platform.XP) {
            return "Windows";
        } else if (capabilities.getPlatform() == Platform.MAC) {
            return "Mac";
        }
        return capabilities.getPlatform().toString();
    }

    /**
     * @since
     * @param capabilities
     * @return
     */
    public static String getBrowserIdentifier(DesiredCapabilities capabilities) {
        String browserName = capabilities.getBrowserName();

        if (BrowserType.IE.equals(browserName)) {
            return "InternetExplorer";
        } else if (BrowserType.FIREFOX.equals(browserName)) {
            return "Firefox";
        } else if (BrowserType.CHROME.equals(browserName)) {
            return "Chrome";
        } else if (BrowserType.SAFARI.equals(browserName)) {
            return "Safari";
        } else if (BrowserType.OPERA.equals(browserName)) {
            return "Opera";
        }

        return browserName;
    }

    @After
    public void checkCompareFailures() {
        if (!screenshotFailures.isEmpty()) {
            throw new ScreenshotComparisonException(
                    "The following screenshots did not match the reference: "
                            + screenshotFailures.toString());
        }

    }

    public static class ScreenshotComparisonException extends RuntimeException {

        public ScreenshotComparisonException() {
            super();
        }

        public ScreenshotComparisonException(String message, Throwable cause) {
            super(message, cause);
        }

        public ScreenshotComparisonException(String message) {
            super(message);
        }

        public ScreenshotComparisonException(Throwable cause) {
            super(cause);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.tests.tb3.AbstractTB3Test#onUncaughtException(java.lang.Throwable
     * )
     */
    @Override
    public void onUncaughtException(Throwable cause) {
        super.onUncaughtException(cause);
        try {
            TestBenchCommands testBench = testBench();
            if (testBench != null) {
                testBench.disableWaitForVaadin();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        try {
            if (driver != null) {
                BufferedImage screenshotImage = ImageIO
                        .read(new ByteArrayInputStream(
                                ((TakesScreenshot) driver)
                                        .getScreenshotAs(OutputType.BYTES)));
                ImageIO.write(screenshotImage, "png", new File(
                        getScreenshotFailureName()));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    /**
     * @since
     * @return
     */
    private String getScreenshotFailureName() {
        return getScreenshotErrorBaseName() + "-failure.png";
    }

    private String getScreenshotReferenceName(String testName,
            DesiredCapabilities capabilities, String identifier) {
        String browserIdentifier = getBrowserIdentifier(capabilities);
        String platform = getPlatform(capabilities);
        String browserVersion = capabilities.getVersion();

        // WindowMaximizeRestoreTest_Windows_InternetExplorer_8_window-1-moved-maximized-restored.png
        return getScreenshotReferenceDirectory() + "/" + testName + "_"
                + platform + "_" + browserIdentifier + "_" + browserVersion
                + "_" + identifier + ".png";
    }

    private String getScreenshotErrorBaseName() {
        return getScreenshotReferenceName(getTestName(),
                getDesiredCapabilities(), "").replace(
                getScreenshotReferenceDirectory(),
                getScreenshotErrorDirectory()).replace("_.png", "");
    }

    @Before
    public void cleanErrorDirectory() {
        // Remove any screenshots for this test from the error directory
        // before running it. Leave unrelated files as-is
        File errorDirectory = new File(getScreenshotErrorDirectory());

        // Create errors directory if it does not exist
        if (!errorDirectory.exists()) {
            errorDirectory.mkdirs();
        }

        final String errorBase = getScreenshotErrorBaseName()
                .replace("\\", "/");
        File[] files = errorDirectory.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                String thisFile = pathname.getAbsolutePath().replace("\\", "/");
                if (thisFile.startsWith(errorBase)) {
                    return true;
                }
                return false;
            }
        });
        for (File f : files) {
            f.delete();
        }
    }
}
