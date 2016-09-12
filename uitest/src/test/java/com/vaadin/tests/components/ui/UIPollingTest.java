package com.vaadin.tests.components.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIPollingTest extends MultiBrowserTest {

    @Test
    public void testPolling() throws Exception {
        openTestURL();
        getTextField().setValue("500");
        sleep(2000);
        /* Ensure polling has taken place */
        Assert.assertTrue("Page does not contain the given text",
                driver.getPageSource().contains("2. 1000ms has passed"));
        getTextField().setValue("-1");
        sleep(2000);
        /* Ensure polling has stopped */
        Assert.assertFalse("Page contains the given text",
                driver.getPageSource().contains("20. 10000ms has passed"));
    }

    public TextFieldElement getTextField() {
        return $(TextFieldElement.class).first();
    }
}
