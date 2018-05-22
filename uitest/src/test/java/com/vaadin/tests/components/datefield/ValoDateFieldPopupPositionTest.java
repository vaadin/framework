package com.vaadin.tests.components.datefield;

import org.junit.Assert;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.DateFieldElement;

/**
 * Test for date field popup calendar position in Valo theme.
 *
 * Test method is defined in super class.
 *
 * @author Vaadin Ltd
 */
public class ValoDateFieldPopupPositionTest extends DateFieldPopupPositionTest {

    @Override
    protected void checkPopupPosition() {
        DateFieldElement field = $(DateFieldElement.class).first();
        WebElement popup = getPopup();
        int left = field.getLocation().getX();
        int popupRight = popup.getLocation().getX()
                + popup.getSize().getWidth();

        Assert.assertTrue(
                "Calendar popup has wrong X coordinate=" + popupRight
                        + " , left side of the field is " + left,
                popupRight <= left);
    }
}
