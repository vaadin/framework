package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

/**
 * @author Vaadin Ltd
 *
 */
public class HorizontalScrollAfterResizeTest extends GridBasicFeaturesTest {

    /**
     * The behavior without the fix differs across different browsers but
     * scenario should work everywhere.
     */
    @Test
    public void scrollAfterResize() {
        getDriver().manage().window().setSize(new Dimension(600, 400));
        openTestURL();
        getDriver().manage().window().setSize(new Dimension(200, 400));

        // First scroll to the right
        scrollGridHorizontallyTo(600);
        Point locationAfterFirstScroll = $(GridElement.class).first()
                .getCell(0, 9).getLocation();

        // resize back
        getDriver().manage().window().setSize(new Dimension(600, 400));
        // shrink again
        getDriver().manage().window().setSize(new Dimension(200, 400));

        // second scroll to the right
        scrollGridHorizontallyTo(600);

        Point locationAfterSecondScrollcation = $(GridElement.class).first()
                .getCell(0, 9).getLocation();

        // With the bug scrolling doesn't happen. Location should be around of
        // the initial scrolling
        assertEquals(locationAfterFirstScroll.getY(),
                locationAfterSecondScrollcation.getY());
        int delta = 5;
        assertTrue(Math.abs(locationAfterFirstScroll.getX()
                - locationAfterSecondScrollcation.getX()) < delta);
    }

    @Override
    protected Class<?> getUIClass() {
        return HorizontalScrollAfterResize.class;
    }
}
