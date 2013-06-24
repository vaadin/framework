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

public abstract class ScreenshotTB3Test extends AbstractTB3Test {

    private List<String> screenshotFailures = new ArrayList<String>();;

    /**
     * @since
     * @param identifier
     * @throws IOException
     */
    protected void compareScreen(String identifier) throws IOException {
        Parameters.setScreenshotErrorDirectory(getScreenshotErrorDirectory());
        Parameters
                .setScreenshotReferenceDirectory(getScreenshotReferenceDirectory());
        File ref = new File(getScreenshotReferenceName(getTestName(),
                getDesiredCapabilities(), identifier));
        try {
            if (!testBench(driver).compareScreen(ref)) {
                throw new AssertionError("comparison failed");
            }
        } catch (AssertionError t) {
            screenshotFailures.add(identifier);
        }
    }

    private String getScreenshotErrorDirectory() {
        return getScreenshotDirectory() + "/errors";
    }

    /**
     * @since
     * @param testName
     * @param desiredCapabilities2
     * @return
     */
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
            testBench().disableWaitForVaadin();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        try {
            BufferedImage screenshotImage = ImageIO
                    .read(new ByteArrayInputStream(((TakesScreenshot) driver)
                            .getScreenshotAs(OutputType.BYTES)));
            ImageIO.write(screenshotImage, "png", new File(
                    getScreenshotFailureName()));
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
