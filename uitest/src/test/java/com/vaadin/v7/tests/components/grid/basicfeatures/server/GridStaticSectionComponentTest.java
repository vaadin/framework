package com.vaadin.v7.tests.components.grid.basicfeatures.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridStaticSectionComponentTest extends GridBasicFeaturesTest {

    @Test
    public void testNativeButtonInHeader() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 1", "Header Type",
                "Widget Header");

        getGridElement().$(ButtonElement.class).first().click();

        assertTrue("Button click should be logged",
                logContainsText("Button clicked!"));
    }

    @Test
    public void testNativeButtonInFooter() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Footer", "Visible");
        selectMenuPath("Component", "Footer", "Append row");
        selectMenuPath("Component", "Columns", "Column 1", "Footer Type",
                "Widget Footer");

        getGridElement().$(ButtonElement.class).first().click();

        assertTrue("Button click should be logged",
                logContainsText("Button clicked!"));
    }

    @Test
    public void testRemoveComponentFromHeader() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Columns", "Column 1", "Header Type",
                "Widget Header");
        selectMenuPath("Component", "Columns", "Column 1", "Header Type",
                "Text Header");
        assertTrue("No notifications should've been shown",
                !$(NotificationElement.class).exists());
        assertEquals("Header should've been reverted back to text header",
                "text header", getGridElement().getHeaderCell(0, 1).getText()
                        .toLowerCase(Locale.ROOT));
    }

}
