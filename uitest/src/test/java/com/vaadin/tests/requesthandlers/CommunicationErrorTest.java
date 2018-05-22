package com.vaadin.tests.requesthandlers;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for null values in communication error json object .
 *
 * @author Vaadin Ltd
 */
public class CommunicationErrorTest extends MultiBrowserTest {

    @Test
    public void testRedirection() {
        openTestURL();

        $(ButtonElement.class).first().click();

        Assert.assertTrue(isElementPresent(By.className("redirected")));
    }

}
