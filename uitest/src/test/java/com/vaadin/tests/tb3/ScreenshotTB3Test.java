package com.vaadin.tests.tb3;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.screenshot.ImageFileUtil;

/**
 * Base class which provides functionality for tests which use the automatic
 * screenshot comparison function.
 *
 * @author Vaadin Ltd
 */
public abstract class ScreenshotTB3Test extends AbstractTB3Test {

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
        screenshotFailures = new ArrayList<>();
        String testClassName = getClass().getSimpleName();
        testBench().setReferenceNameGenerator((identifier, capabilities) -> {
            // Make sure error screenshot directory exists.
            Paths.get(ImageFileUtil.getScreenshotErrorDirectory(),
                    capabilities.getBrowserName().toLowerCase()).toFile()
                    .mkdirs();
            return Paths.get(capabilities.getBrowserName().toLowerCase(),
                    String.format("%s-%s%s%s", testClassName,
                            testName.getMethodName().replace('[', '_')
                                    .replace(']', '_'),
                            getDesiredCapabilities().getVersion(), identifier))
                    .toString();
        });
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
        if (!testBench().compareScreen(identifier)) {
            screenshotFailures.add(testBench().getReferenceNameGenerator()
                    .generateName(identifier,
                            ((HasCapabilities) getDriver()).getCapabilities()));
        }
    }

    protected void compareScreen(WebElement element, String identifier)
            throws IOException {
        if (!((TestBenchElement) element).compareScreen(identifier)) {
            screenshotFailures.add(testBench().getReferenceNameGenerator()
                    .generateName(identifier,
                            ((HasCapabilities) getDriver()).getCapabilities()));
        }
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
}
