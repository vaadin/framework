package com.vaadin.tests.components.ui;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIPollingTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Manually testing IE8 stops polling with -1, but with automated test
        // it seems to be highly unpredictable.
        return super.getBrowsersExcludingIE8();
    }

    @Test
    public void testPolling() throws Exception {
        openTestURL();
        getTextField().setValue("500");
        sleep(2000);
        /* Ensure polling has taken place */
        Assert.assertTrue("Page does not contain the given text", driver
                .getPageSource().contains("2. 1000ms has passed"));
        getTextField().setValue("-1");
        sleep(2000);
        /* Ensure polling has stopped */
        Assert.assertFalse("Page contains the given text", driver
                .getPageSource().contains("20. 10000ms has passed"));
    }

    public TextFieldElement getTextField() {
        return $(TextFieldElement.class).first();
    }
}
