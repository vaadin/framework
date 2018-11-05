package com.vaadin.tests.components.nativebutton;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if coordinates returned by click event on NativeButtons look
 * good. (see #14022)
 *
 * @author Vaadin Ltd
 */
public class NativeButtonClickTest extends MultiBrowserTest {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.tb3.MultiBrowserTest#getBrowsersToTest()
     */

    @Test
    public void testClickCoordinates() {
        openTestURL();

        clickFirstButton();
        String eventCoordinates = getFirstLabelValue();
        assertNotEquals("0,0", eventCoordinates);

        clickSecondButton();
        eventCoordinates = getSecondLabelValue();
        assertNotEquals("0,0", eventCoordinates);
    }

    private void clickFirstButton() {
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
    }

    private void clickSecondButton() {
        ButtonElement button = $(ButtonElement.class).get(1);
        button.click();
    }

    private String getFirstLabelValue() {
        LabelElement label = $(LabelElement.class).get(1);
        return label.getText();
    }

    private String getSecondLabelValue() {
        LabelElement label = $(LabelElement.class).get(2);
        return label.getText();
    }
}
