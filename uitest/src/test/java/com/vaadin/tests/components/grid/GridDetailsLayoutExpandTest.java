package com.vaadin.tests.components.grid;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests the layouting of Grid's details row when it contains a HorizontalLayout
 * with expand ratios.
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridDetailsLayoutExpandTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> browsersToTest = super.getBrowsersToTest();
        // TODO: remove when #19326 is fixed
        browsersToTest.remove(Browser.IE8.getDesiredCapabilities());
        browsersToTest.remove(Browser.IE9.getDesiredCapabilities());
        // for some reason PhantomJS doesn't find the label even if it detects
        // the presence
        browsersToTest.remove(Browser.PHANTOMJS.getDesiredCapabilities());
        return browsersToTest;
    }

    @Test
    public void testLabelWidths() {
        openTestURL();
        waitForElementPresent(By.className("v-grid"));

        GridElement grid = $(GridElement.class).first();
        int gridWidth = grid.getSize().width;

        grid.getRow(2).click();
        waitForElementPresent(By.id("lbl2"));

        // space left over from first label should be divided equally
        double expectedWidth = (double) (gridWidth - 200) / 2;
        assertLabelWidth("lbl2", expectedWidth);
        assertLabelWidth("lbl3", expectedWidth);
    }

    private void assertLabelWidth(String id, double expectedWidth) {
        // 1px leeway for calculations
        assertThat("Unexpected label width.",
                (double) $(LabelElement.class).id(id).getSize().width,
                closeTo(expectedWidth, 1d));
    }
}
