package com.vaadin.tests.components.grid.basics;

import java.util.List;
import java.util.stream.Stream;

import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.customelements.GridElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Base class for all {@link GridBasics} UI tests
 */
public abstract class GridBasicsTest extends MultiBrowserTest {

    /* Identical List of test data */
    private List<DataObject> testData;

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Most tests are run with only one browser.
        return getBrowserCapabilities(Browser.PHANTOMJS);
    }

    @Override
    protected Class<?> getUIClass() {
        return GridBasics.class;
    }

    @Before
    public void setUp() {
        openTestURL();
        testData = DataObject.generateObjects();
    }

    protected GridElement getGridElement() {
        return $(GridElement.class).first();
    }

    protected Stream<DataObject> getTestData() {
        return testData.stream();
    }

    protected void scrollGridVerticallyTo(double px) {
        executeScript("arguments[0].scrollTop = " + px,
                getGridVerticalScrollbar());
    }

    protected void scrollGridHorizontallyTo(double px) {
        executeScript("arguments[0].scrollLeft = " + px,
                getGridHorizontalScrollbar());
    }

    protected WebElement getGridVerticalScrollbar() {
        return getDriver().findElement(By.xpath(
                "//div[contains(@class, \"v-grid-scroller-vertical\")]"));
    }

    protected WebElement getGridHorizontalScrollbar() {
        return getDriver().findElement(By.xpath(
                "//div[contains(@class, \"v-grid-scroller-horizontal\")]"));
    }

}
