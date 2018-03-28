package com.vaadin.tests.components.gridlayout;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for TOP_CENTER and TOP_RIGHT alignments in VerticalLayout.
 *
 * @author Vaadin Ltd
 */
public class ComponentAlignmentsTest extends MultiBrowserTest {

    @Test
    public void testTopCenterAlignment() {
        openTestURL();

        CheckBoxElement checkbox = $(CheckBoxElement.class).first();
        WebElement parent = checkbox.findElement(By.xpath(".."));

        int leftSpaceSize = checkbox.getLocation().getX()
                - parent.getLocation().getX();
        int rightSpaceSize = parent.getLocation().getX()
                + parent.getSize().getWidth() - checkbox.getLocation().getX()
                - checkbox.getSize().getWidth();
        assertTrue("No space on the left for centered element",
                leftSpaceSize > 0);
        assertTrue("No space on the right for centered element",
                rightSpaceSize > 0);

        int diff = Math.abs(rightSpaceSize - leftSpaceSize);
        // IE11 2pixels
        assertTrue("Element is not in the center, diff:" + diff, diff <= 2);
    }

    @Test
    public void testTopRightAlignment() {
        openTestURL();

        CheckBoxElement checkbox = $(CheckBoxElement.class).get(1);
        WebElement parent = checkbox.findElement(By.xpath(".."));

        int leftSpaceSize = checkbox.getLocation().getX()
                - parent.getLocation().getX();
        int rightSpaceSize = parent.getLocation().getX()
                + parent.getSize().getWidth() - checkbox.getLocation().getX()
                - checkbox.getSize().getWidth();
        assertTrue("No space on the left for centered element",
                leftSpaceSize > 0);
        assertTrue("There is some space on the right for the element",
                rightSpaceSize <= 1);

        int sizeDiff = parent.getSize().getWidth()
                - checkbox.getSize().getWidth();
        assertTrue("Element is not in aligned to the right",
                Math.abs(sizeDiff - leftSpaceSize) <= 1);
    }
}
