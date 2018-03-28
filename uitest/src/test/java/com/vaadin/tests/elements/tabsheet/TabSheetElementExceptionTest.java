package com.vaadin.tests.elements.tabsheet;

import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that an exception is thrown when attempting to select a tab that does
 * not exist in the tab sheet.
 */
public class TabSheetElementExceptionTest extends MultiBrowserTest {

    @Test
    public void testNoExceptionWhenFound() {
        openTestURL();
        TabSheetElement tse = $(TabSheetElement.class).first();
        for (int i = 1; i <= 5; i++) {
            tse.openTab("Tab " + i);
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testExceptionWhenNotFound() {
        openTestURL();
        TabSheetElement tse = $(TabSheetElement.class).first();
        tse.openTab("Tab 6");
    }
}