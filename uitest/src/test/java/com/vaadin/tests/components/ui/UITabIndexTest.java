package com.vaadin.tests.components.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.UIElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UITabIndexTest extends MultiBrowserTest {

    @Test
    public void testTabIndexOnUIRoot() throws Exception {
        openTestURL();
        assertTabIndex("1");
        $(ButtonElement.class).first().click();
        assertTabIndex("-1");
        $(ButtonElement.class).get(1).click();
        assertTabIndex("0");
        $(ButtonElement.class).get(2).click();
        assertTabIndex("1");
    }

    private void assertTabIndex(String expected) {
        Assert.assertEquals("Unexpected tab index,", expected,
                $(UIElement.class).first().getAttribute("tabIndex"));
    }
}
