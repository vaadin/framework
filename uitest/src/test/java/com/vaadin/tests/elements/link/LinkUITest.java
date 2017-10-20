package com.vaadin.tests.elements.link;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.LinkElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class LinkUITest extends MultiBrowserTest {
    LinkElement link;

    @Before
    public void init() {
        openTestURL();
        link = $(LinkElement.class).first();
    }

    @Test
    public void testLinkClick() {
        String currentUrl = getDriver().getCurrentUrl();
        assertTrue("Current URL " + currentUrl + " should end with LinkUI?",
                currentUrl.endsWith("LinkUI"));
        link.click();
        currentUrl = getDriver().getCurrentUrl();
        assertFalse(
                "Current URL " + currentUrl + " should not end with LinkUI?",
                currentUrl.endsWith("LinkUI"));

    }

    @Test
    public void getLinkCaption() {
        assertEquals("server root", link.getCaption());
    }

}
