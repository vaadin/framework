package com.vaadin.tests.components.grid;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that details row displays GridLayout contents properly.
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridLayoutDetailsRowTest extends MultiBrowserTest {

    @Test
    public void testLabelHeights() {
        openTestURL();
        waitForElementPresent(By.className("v-grid"));

        GridElement grid = $(GridElement.class).first();

        grid.getRow(2).click(5, 5);
        waitForElementPresent(By.id("lbl2"));

        GridLayoutElement gridLayout = $(GridLayoutElement.class).first();
        int gridLayoutHeight = gridLayout.getSize().height;

        // height should be divided equally
        double expectedHeight = gridLayoutHeight / 4;
        assertLabelHeight("lbl1", expectedHeight);
        assertLabelHeight("lbl2", expectedHeight);
        assertLabelHeight("lbl3", expectedHeight);
        assertLabelHeight("lbl4", expectedHeight);
    }

    private void assertLabelHeight(String id, double expectedHeight) {
        // 1px leeway for calculations
        assertThat("Unexpected label height.",
                (double) $(LabelElement.class).id(id).getSize().height,
                closeTo(expectedHeight, 1d));
    }
}
