package com.vaadin.tests.elements.table;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableHeaderElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GetRowHeaderTest extends MultiBrowserTest {

    TableElement table;
    private static final int COLUMN_INDEX = 0;

    @Override
    protected Class<?> getUIClass() {
        return TableScroll.class;
    }

    @Before
    public void init() {
        openTestURL();
        table = $(TableElement.class).first();
    }

    @Test
    public void testGetRowHeaderCaption() {
        TableHeaderElement header = table.getHeaderCell(COLUMN_INDEX);
        String expected = "property0";
        String actual = header.getCaption();
        Assert.assertEquals(
                "TableHeaderElement.getCaption() returns wrong value.",
                expected, actual);
    }

    // Test that clicking on the header sorts the column
    @Test
    public void testTableRowHeaderSort() {
        TableHeaderElement header = table.getHeaderCell(COLUMN_INDEX);
        // sort in asc order
        header.click();
        table.waitForVaadin();
        // sort in desc order
        header.click();
        table.waitForVaadin();
        String expected = "col=0 row=99";
        String actual = table.getCell(0, COLUMN_INDEX).getText();
        Assert.assertEquals(
                "TableHeaderElement.toggleSort() did not sort column "
                        + COLUMN_INDEX,
                expected, actual);
    }

    @Test
    public void testTableRowHeaderGetHandle() {
        TableHeaderElement header = table.getHeaderCell(COLUMN_INDEX);
        int initialWidth = header.getSize().width;
        WebElement handle = header.getResizeHandle();
        Actions builder = new Actions(getDriver());
        /*
         * To workaround a bug with clicking on small elements(not precise
         * cursor positioning) in IE we do the following: 1. Extend the resize
         * handle element (it's initial width is 3) 2. Resize the column. 3.
         * Resize the handle back to initial value.
         */
        if (BrowserUtil.isIE(getDesiredCapabilities())) {
            int initHandleWidth = handle.getSize().width;
            setElementWidth(handle, 20);
            handle = header.getResizeHandle();
            builder.clickAndHold(handle).moveByOffset(-20, 0).release().build()
                    .perform();
            setElementWidth(handle, initHandleWidth);
        } else {
            builder.clickAndHold(handle).moveByOffset(-20, 0).release().build()
                    .perform();
        }
        header = table.getHeaderCell(COLUMN_INDEX);
        int widthAfterResize = header.getSize().width;
        Assert.assertTrue(
                "The column with index " + COLUMN_INDEX + " was not resized.",
                initialWidth > widthAfterResize);
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetHeaderCellException() {
        table.getHeaderCell(-10);
    }

    private void setElementWidth(WebElement elem, int width) {
        JavascriptExecutor js = getCommandExecutor();
        String jsScript = "var elem=arguments[0];" + "elem.style.width='"
                + width + "px';";
        js.executeScript(jsScript, elem);
    }
}
