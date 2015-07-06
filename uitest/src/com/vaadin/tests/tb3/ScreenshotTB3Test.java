/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.tests.tb3;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.screenshot.ImageFileUtil;

/**
 * Base class which provides functionality for tests which use the automatic
 * screenshot comparison function.
 * 
 * @author Vaadin Ltd
 */
public abstract class ScreenshotTB3Test extends AbstractTB3Test {

    @Rule
    public ScreenshotOnFailureRule screenshotOnFailure = new ScreenshotOnFailureRule(
            this, true) {

        @Override
        protected void failed(Throwable throwable, Description description) {
            super.failed(throwable, description);
            closeApplication();
        }

        @Override
        protected void succeeded(Description description) {
            super.succeeded(description);
            closeApplication();
        }

        @Override
        protected File getErrorScreenshotFile(Description description) {
            return ImageFileUtil
                    .getErrorScreenshotFile(getScreenshotFailureName());
        };
    };

    private String screenshotBaseName;

    @Rule
    public TestRule watcher = new TestWatcher() {

        @Override
        protected void starting(org.junit.runner.Description description) {
            Class<?> testClass = description.getTestClass();
            // Runner adds [BrowserName] which we do not want to use in the
            // screenshot name
            String testMethod = description.getMethodName();
            testMethod = testMethod.replaceAll("\\[.*\\]", "");

            String className = testClass.getSimpleName();
            screenshotBaseName = className + "-" + testMethod;
        }
    };

    /**
     * Contains a list of screenshot identifiers for which
     * {@link #compareScreen(String)} has failed during the test
     */
    private List<String> screenshotFailures;

    /**
     * Defines TestBench screen comparison parameters before each test run
     */
    @Before
    public void setupScreenComparisonParameters() {
        screenshotFailures = new ArrayList<String>();

        Parameters.setScreenshotErrorDirectory(getScreenshotErrorDirectory());
        Parameters
                .setScreenshotReferenceDirectory(getScreenshotReferenceDirectory());
    }

    /**
     * Grabs a screenshot and compares with the reference image with the given
     * identifier. Supports alternative references and will succeed if the
     * screenshot matches at least one of the references.
     * 
     * In case of a failed comparison this method stores the grabbed screenshots
     * in the error directory as defined by
     * {@link #getScreenshotErrorDirectory()}. It will also generate a html file
     * in the same directory, comparing the screenshot with the first found
     * reference.
     * 
     * @param identifier
     * @throws IOException
     */
    protected void compareScreen(String identifier) throws IOException {
        compareScreen(null, identifier);
    }

    protected void compareScreen(WebElement element, String identifier)
            throws IOException {
        if (identifier == null || identifier.isEmpty()) {
            throw new IllegalArgumentException("Empty identifier not supported");
        }

        File mainReference = getScreenshotReferenceFile(identifier);

        List<File> referenceFiles = findReferenceAndAlternatives(mainReference);
        List<File> failedReferenceFiles = new ArrayList<File>();

        for (File referenceFile : referenceFiles) {
            boolean match = false;
            if (element == null) {
                // Full screen
                match = testBench(driver).compareScreen(referenceFile);
            } else {
                // Only the element
                match = customTestBench(driver).compareScreen(element,
                        referenceFile,
                        BrowserUtil.isIE8(getDesiredCapabilities()));
            }
            if (match) {
                // There might be failure files because of retries in TestBench.
                deleteFailureFiles(getErrorFileFromReference(referenceFile));
                break;
            } else {
                failedReferenceFiles.add(referenceFile);
            }
        }

        File referenceToKeep = null;
        if (failedReferenceFiles.size() == referenceFiles.size()) {
            // Ensure we use the correct browser version (e.g. if running IE11
            // and only an IE 10 reference was available, then mainReference
            // will be for IE 10, not 11)
            String originalName = getScreenshotReferenceName(identifier);
            File exactVersionFile = new File(originalName);

            if (!exactVersionFile.equals(mainReference)) {
                // Rename png+html to have the correct version
                File correctPng = getErrorFileFromReference(exactVersionFile);
                File producedPng = getErrorFileFromReference(mainReference);
                File correctHtml = htmlFromPng(correctPng);
                File producedHtml = htmlFromPng(producedPng);

                producedPng.renameTo(correctPng);
                producedHtml.renameTo(correctHtml);
                referenceToKeep = exactVersionFile;
                screenshotFailures.add(exactVersionFile.getName());
            } else {
                // All comparisons failed, keep the main error image + HTML
                screenshotFailures.add(mainReference.getName());
                referenceToKeep = mainReference;
            }
        }

        // Remove all PNG/HTML files we no longer need (failed alternative
        // references or all error files (PNG/HTML) if comparison succeeded)
        for (File failedAlternative : failedReferenceFiles) {
            File failurePng = getErrorFileFromReference(failedAlternative);
            if (failedAlternative != referenceToKeep) {
                // Delete png + HTML
                deleteFailureFiles(failurePng);
            }
        }
        if (referenceToKeep != null) {
            File errorPng = getErrorFileFromReference(referenceToKeep);
            enableAutoswitch(new File(errorPng.getParentFile(),
                    errorPng.getName() + ".html"));
        }
    }

    private CustomTestBenchCommandExecutor customTestBench = null;

    private CustomTestBenchCommandExecutor customTestBench(WebDriver driver) {
        if (customTestBench == null) {
            customTestBench = new CustomTestBenchCommandExecutor(driver);
        }

        return customTestBench;
    }

    private void enableAutoswitch(File htmlFile) throws FileNotFoundException,
            IOException {
        if (htmlFile == null || !htmlFile.exists()) {
            return;
        }

        String html = FileUtils.readFileToString(htmlFile);

        html = html.replace("body onclick=\"",
                "body onclick=\"clearInterval(autoSwitch);");
        html = html.replace("</script>",
                ";autoSwitch=setInterval(switchImage,500);</script>");

        FileUtils.writeStringToFile(htmlFile, html);
    }

    private void deleteFailureFiles(File failurePng) {
        File failureHtml = htmlFromPng(failurePng);

        failurePng.delete();
        failureHtml.delete();
    }

    /**
     * Returns a new File which points to a .html file instead of the given .png
     * file
     * 
     * @param png
     * @return
     */
    private static File htmlFromPng(File png) {
        return new File(png.getParentFile(), png.getName().replaceAll(
                "\\.png$", ".png.html"));
    }

    /**
     * 
     * @param referenceFile
     *            The reference image file (in the directory defined by
     *            {@link #getScreenshotReferenceDirectory()})
     * @return the file name of the file generated in the directory defined by
     *         {@link #getScreenshotErrorDirectory()} if comparison with the
     *         given reference image fails.
     */
    private File getErrorFileFromReference(File referenceFile) {

        String absolutePath = referenceFile.getAbsolutePath();
        String screenshotReferenceDirectory = getScreenshotReferenceDirectory();
        String screenshotErrorDirectory = getScreenshotErrorDirectory();
        // We throw an exception to safeguard against accidental reference
        // deletion. See (#14446)
        if (!absolutePath.contains(screenshotReferenceDirectory)) {
            throw new IllegalStateException(
                    "Reference screenshot not in reference directory. Screenshot path: '"
                            + absolutePath + "', directory path: '"
                            + screenshotReferenceDirectory + "'");
        }
        return new File(absolutePath.replace(screenshotReferenceDirectory,
                screenshotErrorDirectory));
    }

    /**
     * Finds alternative references for the given files
     * 
     * @param reference
     * @return all references which should be considered when comparing with the
     *         given files, including the given reference
     */
    private List<File> findReferenceAndAlternatives(File reference) {
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

    /**
     * @param testName
     * @return the reference file name to use for the given browser, as
     *         described by {@literal capabilities}, and identifier
     */
    private File getScreenshotReferenceFile(String identifier) {
        DesiredCapabilities capabilities = getDesiredCapabilities();

        String originalName = getScreenshotReferenceName(identifier);
        File exactVersionFile = new File(originalName);
        if (exactVersionFile.exists()) {
            return exactVersionFile;
        }

        String browserVersion = capabilities.getVersion();

        if (browserVersion.matches("\\d+")) {
            for (int version = Integer.parseInt(browserVersion); version > 0; version--) {
                String fileName = getScreenshotReferenceName(identifier,
                        version);
                File oldVersionFile = new File(fileName);
                if (oldVersionFile.exists()) {
                    return oldVersionFile;
                }
            }
        }

        return exactVersionFile;
    }

    /**
     * @return the base directory of 'reference' and 'errors' screenshots
     */
    protected abstract String getScreenshotDirectory();

    /**
     * @return the base directory of 'reference' and 'errors' screenshots with a
     *         trailing file separator
     */
    private String getScreenshotDirectoryWithTrailingSeparator() {
        String screenshotDirectory = getScreenshotDirectory();
        if (!screenshotDirectory.endsWith(File.separator)) {
            screenshotDirectory += File.separator;
        }
        return screenshotDirectory;
    }

    /**
     * @return the directory where reference images are stored (the 'reference'
     *         folder inside the screenshot directory)
     */
    private String getScreenshotReferenceDirectory() {
        return getScreenshotDirectoryWithTrailingSeparator() + "reference";
    }

    /**
     * @return the directory where comparison error images should be created
     *         (the 'errors' folder inside the screenshot directory)
     */
    private String getScreenshotErrorDirectory() {
        return getScreenshotDirectoryWithTrailingSeparator() + "errors";
    }

    /**
     * Checks if any screenshot comparisons failures occurred during the test
     * and combines all comparison errors into one exception
     * 
     * @throws IOException
     *             If there were failures during the test
     */
    @After
    public void checkCompareFailures() throws IOException {
        if (screenshotFailures != null && !screenshotFailures.isEmpty()) {
            throw new IOException(
                    "The following screenshots did not match the reference: "
                            + screenshotFailures.toString());
        }

    }

    /**
     * @return the name of a "failure" image which is stored in the folder
     *         defined by {@link #getScreenshotErrorDirectory()} when the test
     *         fails
     */
    private String getScreenshotFailureName() {
        return getScreenshotBaseName() + "_"
                + getUniqueIdentifier(getDesiredCapabilities())
                + "-failure.png";
    }

    /**
     * @return the base name used for screenshots. This is the first part of the
     *         screenshot file name, typically created as "testclass-testmethod"
     */
    public String getScreenshotBaseName() {
        return screenshotBaseName;
    }

    /**
     * Returns the name of the reference file based on the given parameters.
     * 
     * @param testName
     * @param capabilities
     * @param identifier
     * @return the full path of the reference
     */
    private String getScreenshotReferenceName(String identifier) {
        return getScreenshotReferenceName(identifier, null);
    }

    /**
     * Returns the name of the reference file based on the given parameters. The
     * version given in {@literal capabilities} is used unless it is overridden
     * by the {@literal versionOverride} parameter.
     * 
     * @param testName
     * @param capabilities
     * @param identifier
     * @return the full path of the reference
     */
    private String getScreenshotReferenceName(String identifier,
            Integer versionOverride) {
        String uniqueBrowserIdentifier;
        if (versionOverride == null) {
            uniqueBrowserIdentifier = getUniqueIdentifier(getDesiredCapabilities());
        } else {
            uniqueBrowserIdentifier = getUniqueIdentifier(
                    getDesiredCapabilities(), "" + versionOverride);
        }

        // WindowMaximizeRestoreTest_Windows_InternetExplorer_8_window-1-moved-maximized-restored.png
        return getScreenshotReferenceDirectory() + File.separator
                + getScreenshotBaseName() + "_" + uniqueBrowserIdentifier + "_"
                + identifier + ".png";
    }

    /**
     * Returns a string which uniquely (enough) identifies this browser. Used
     * mainly in screenshot names.
     * 
     * @param capabilities
     * @param versionOverride
     * 
     * @return a unique string for each browser
     */
    private String getUniqueIdentifier(DesiredCapabilities capabilities,
            String versionOverride) {
        return getUniqueIdentifier(BrowserUtil.getPlatform(capabilities),
                BrowserUtil.getBrowserIdentifier(capabilities), versionOverride);
    }

    /**
     * Returns a string which uniquely (enough) identifies this browser. Used
     * mainly in screenshot names.
     * 
     * @param capabilities
     * 
     * @return a unique string for each browser
     */
    private String getUniqueIdentifier(DesiredCapabilities capabilities) {
        return getUniqueIdentifier(BrowserUtil.getPlatform(capabilities),
                BrowserUtil.getBrowserIdentifier(capabilities),
                capabilities.getVersion());
    }

    private String getUniqueIdentifier(String platform, String browser,
            String version) {
        return platform + "_" + browser + "_" + version;
    }

    /**
     * Returns the base name of the screenshot in the error directory. This is a
     * name so that all files matching {@link #getScreenshotErrorBaseName()}*
     * are owned by this test instance (taking into account
     * {@link #getDesiredCapabilities()}) and can safely be removed before
     * running this test.
     */
    private String getScreenshotErrorBaseName() {
        return getScreenshotReferenceName("dummy", null).replace(
                getScreenshotReferenceDirectory(),
                getScreenshotErrorDirectory()).replace("_dummy.png", "");
    }

    /**
     * Removes any old screenshots related to this test from the errors
     * directory before running the test
     */
    @Before
    public void cleanErrorDirectory() {
        // Remove any screenshots for this test from the error directory
        // before running it. Leave unrelated files as-is
        File errorDirectory = new File(getScreenshotErrorDirectory());

        // Create errors directory if it does not exist
        if (!errorDirectory.exists()) {
            errorDirectory.mkdirs();
        }

        final String errorBase = getScreenshotErrorBaseName();
        File[] files = errorDirectory.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                String thisFile = pathname.getAbsolutePath();
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
