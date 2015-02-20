package com.vaadin.tests.application;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class RefreshStatePreserveTest extends MultiBrowserTest {
    private static String UI_ID_TEXT = "UI id: 0";

    @Test
    public void testPreserveState() throws Exception {
        openTestURL();
        assertCorrectState();
        // URL needs to be different or some browsers don't count it as history
        openTestURL("debug");
        assertCorrectState();
        executeScript("history.back()");
        assertCorrectState();
    }

    private void assertCorrectState() {
        waitForElementPresent(By.className("v-label"));
        LabelElement uiIdLabel = $(LabelElement.class).get(7);
        Assert.assertEquals("Incorrect UI id,", UI_ID_TEXT, uiIdLabel.getText());
    }
}
