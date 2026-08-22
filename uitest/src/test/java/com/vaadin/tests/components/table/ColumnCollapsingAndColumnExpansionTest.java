package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableElement.ContextMenuElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ColumnCollapsingAndColumnExpansionTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return Arrays.asList(Browser.CHROME.getDesiredCapabilities(),
                Browser.FIREFOX.getDesiredCapabilities());
    }

    @Test
    public void expandCorrectlyAfterCollapse() throws IOException {
        openTestURL();

        TableElement table = $(TableElement.class).first();

        // Hide col2 through UI
        table.openCollapseMenu().getItem(1).click();
        compareScreen(table, "col1-col3");

        // Hide col1 using button
        ButtonElement hide1 = $(ButtonElement.class).caption("Collapse Col1")
                .first();
        hide1.click();
        compareScreen(table, "col3");

        // Show column 2 using context menu (first action)
        contextClick(table.getCell(0, 0));
        ContextMenuElement contextMenu = table.getContextMenu();
        WebElement i = contextMenu.getItem(0);
        i.click();
        compareScreen(table, "col2-col3");

        // Show column 1 again
        ButtonElement show1 = $(ButtonElement.class).caption("Show Col1")
                .first();
        show1.click();

        compareScreen(table, "col1-col2-col3");
    }

    private void contextClick(TestBenchElement e) {
        if (BrowserUtil.isPhantomJS(getDesiredCapabilities())) {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            String scr = "var element=arguments[0];"
                    + "var ev = document.createEvent('HTMLEvents');"
                    + "ev.initEvent('contextmenu', true, false);"
                    + "element.dispatchEvent(ev);";
            js.executeScript(scr, e);
        } else {
            e.contextClick();
        }

    }

    @Test
    public void collapseEvents() {
        openTestURL();
        TableElement table = $(TableElement.class).first();

        // Through menu
        table.openCollapseMenu().getItem(0).click();
        assertEquals("1. Collapse state for Col1 changed to true",
                getLogRow(0));

        // Through button
        $(ButtonElement.class).caption("Collapse Col2").first().click();
        assertEquals("2. Collapse state for Col2 changed to true",
                getLogRow(0));

        // Show through menu
        table.openCollapseMenu().getItem(1).click();
        assertEquals("3. Collapse state for Col1 changed to false",
                getLogRow(0));

        // Show through button
        $(ButtonElement.class).caption("Show Col2").first().click();
        assertEquals("4. Collapse state for Col2 changed to false",
                getLogRow(0));

    }
}
