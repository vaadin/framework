package com.vaadin.tests.components.grid.basics;

import java.util.List;
import java.util.stream.Stream;

import org.junit.Before;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.GridElement;
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

    protected GridElement getGrid() {
        return $(GridElement.class).first();
    }

    protected Stream<DataObject> getTestData() {
        return testData.stream();
    }
}
