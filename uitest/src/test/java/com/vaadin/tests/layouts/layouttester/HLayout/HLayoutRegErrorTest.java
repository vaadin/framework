package com.vaadin.tests.layouts.layouttester.HLayout;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;

import com.vaadin.testbench.elements.HorizontalLayoutElement;
import com.vaadin.tests.layouts.layouttester.BaseLayoutRegErrorTest;

public class HLayoutRegErrorTest extends BaseLayoutRegErrorTest {

    @Override
    public void LayoutRegError() throws IOException {
        super.LayoutRegError();

        // The layout is too wide to fit into one screenshot, we need to scroll
        // and take two more.

        List<HorizontalLayoutElement> layouts = $(HorizontalLayoutElement.class)
                .all();
        assertEquals(10, layouts.size());
        HorizontalLayoutElement group2row1 = layouts.get(2);
        HorizontalLayoutElement group2row2 = layouts.get(7);
        HorizontalLayoutElement group3row1 = layouts.get(3);
        HorizontalLayoutElement group3row2 = layouts.get(8);
        HorizontalLayoutElement lastOfRow1 = layouts.get(4);
        HorizontalLayoutElement lastOfRow2 = layouts.get(9);

        // scroll to both to ensure both contents are fully in view,
        // moveToElement fails on Firefox since the component is out of viewport
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", group3row1);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", group3row2);
        // scroll back to get the previous group at the left edge
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", group2row1);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", group2row2);

        compareScreen("RegError-Scrolled-Middle");

        // scroll to last ones
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", lastOfRow1);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", lastOfRow2);

        compareScreen("RegError-Scrolled-End");
    }
}
