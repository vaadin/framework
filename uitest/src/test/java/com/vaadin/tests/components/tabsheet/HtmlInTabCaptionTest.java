package com.vaadin.tests.components.tabsheet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.AccordionElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class HtmlInTabCaptionTest extends SingleBrowserTest {
    static final String PLAIN_TEXT_RED = "<font color='red'>red</font>";
    static final String HTML_TEXT_RED = "red";
    static final String PLAIN_TEXT_BLUE = "<font color='blue'>blue</font>";
    static final String HTML_TEXT_BLUE = "blue";

    @Test
    public void tabsheetWithoutHtmlCaptions() {
        openTestURL();
        TabSheetElement ts = $(TabSheetElement.class).get(0);
        assertEquals(PLAIN_TEXT_RED, getTab(ts, 0).getText());
        assertEquals(PLAIN_TEXT_BLUE, getTab(ts, 1).getText());
    }

    private WebElement getTab(TabSheetElement tabSheetElement, int i) {
        String className = "v-tabsheet-tabitem";
        if (tabSheetElement instanceof AccordionElement) {
            className = "v-accordion-item";
        }
        return tabSheetElement.findElements(By.className(className)).get(i);
    }

    @Test
    public void tabsheetWithHtmlCaptions() {
        openTestURL();
        TabSheetElement ts = $(TabSheetElement.class).get(1);
        assertEquals(HTML_TEXT_RED, getTab(ts, 0).getText());
        assertEquals(HTML_TEXT_BLUE, getTab(ts, 1).getText());
    }

    @Test
    public void accordionWithoutHtmlCaptions() {
        openTestURL();
        AccordionElement acc = $(AccordionElement.class).get(0);
        assertEquals(PLAIN_TEXT_RED, getTab(acc, 0).getText());
        assertEquals(PLAIN_TEXT_BLUE, getTab(acc, 1).getText());

    }

    @Test
    public void accordionWithHtmlCaptions() {
        openTestURL();
        AccordionElement acc = $(AccordionElement.class).get(1);
        assertEquals(HTML_TEXT_RED, getTab(acc, 0).getText());
        assertEquals(HTML_TEXT_BLUE, getTab(acc, 1).getText());
    }
}
