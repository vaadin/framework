package com.vaadin.v7.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeatures;

/**
 * Abstract base class for header and footer tests.
 *
 * @author Vaadin Ltd
 */
public abstract class GridStaticSectionTest
        extends GridBasicClientFeaturesTest {

    protected void assertHeaderTexts(int headerId, int rowIndex) {
        int i = 0;
        for (TestBenchElement cell : getGridElement()
                .getHeaderCells(rowIndex)) {
            WebElement content = cell.findElement(By.tagName("div"));

            if (i % 3 == 0) {
                assertText(String.format("Header (%d,%d)", headerId, i),
                        content);
            } else if (i % 2 == 0) {
                assertHTML(String.format("<b>Header (%d,%d)</b>", headerId, i),
                        content);
            } else {
                assertHTML(String.format(
                        "<div class=\"gwt-HTML\">Header (%d,%d)</div>",
                        headerId, i), content);
            }

            i++;
        }
        assertEquals("number of header columns", GridBasicFeatures.COLUMNS, i);
    }

    protected void assertFooterTexts(int footerId, int rowIndex) {
        int i = 0;
        for (TestBenchElement cell : getGridElement()
                .getFooterCells(rowIndex)) {
            WebElement content = cell.findElement(By.tagName("div"));

            if (i % 3 == 0) {
                assertText(String.format("Footer (%d,%d)", footerId, i),
                        content);
            } else if (i % 2 == 0) {
                assertHTML(String.format("<b>Footer (%d,%d)</b>", footerId, i),
                        content);
            } else {
                assertHTML(String.format(
                        "<div class=\"gwt-HTML\">Footer (%d,%d)</div>",
                        footerId, i), content);
            }
            i++;
        }
        assertEquals("number of footer columns", GridBasicFeatures.COLUMNS, i);
    }

    protected static void assertText(String text, WebElement e) {
        // TBE.getText returns "" if the element is scrolled out of view
        assertEquals(text, e.getAttribute("innerHTML"));
    }

    protected static void assertHTML(String text, WebElement e) {
        String html = e.getAttribute("innerHTML");

        // IE 8 returns tags as upper case while other browsers do not, make the
        // comparison non-casesensive
        html = html.toLowerCase(Locale.ROOT);
        text = text.toLowerCase(Locale.ROOT);

        // IE 8 returns attributes without quotes, make the comparison without
        // quotes
        html = html.replaceAll("\"", "");
        text = html.replaceAll("\"", "");

        assertEquals(text, html);
    }
}
