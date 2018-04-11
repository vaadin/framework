package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertTrue;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.DateFieldElement;

/**
 * Test for date field popup calendar position in reindeer theme.
 *
 * Test method is defined in super class.
 *
 * @author Vaadin Ltd
 */
public class ReindeerDateFieldPopupPositionTest
        extends DateFieldPopupPositionTest {

    @Override
    protected void checkPopupPosition() {
        DateFieldElement field = $(DateFieldElement.class).first();
        int right = field.getLocation().getX() + field.getSize().getWidth();
        WebElement popup = getPopup();

        assertTrue(
                "Calendar popup has wrong X coordinate="
                        + popup.getLocation().getX()
                        + " , right side of the field is " + right,
                popup.getLocation().getX() >= right);
    }
}
