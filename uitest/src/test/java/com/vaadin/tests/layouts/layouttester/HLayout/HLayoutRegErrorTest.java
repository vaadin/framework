package com.vaadin.tests.layouts.layouttester.HLayout;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.interactions.Actions;

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

        // scroll to both to ensure both contents are fully in view
        new Actions(driver).moveToElement(group3row1).build().perform();
        new Actions(driver).moveToElement(group3row2).build().perform();
        // scroll back to get the previous group at the left edge
        new Actions(driver).moveToElement(group2row1).build().perform();
        new Actions(driver).moveToElement(group2row2).build().perform();

        compareScreen("RegError-Scrolled-Middle");

        // scroll to last ones
        new Actions(driver).moveToElement(lastOfRow1).build().perform();
        new Actions(driver).moveToElement(lastOfRow2).build().perform();

        compareScreen("RegError-Scrolled-End");
    }
}
