package com.vaadin.tests.components.tabsheet;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TabSheetInSplitPanelTest extends MultiBrowserTest {

    @Test
    public void ensureNoScrollbars() {
        openTestURL();
        TabSheetElement ts = $(TabSheetElement.class).first();
        List<WebElement> scrollables = ts
                .findElements(By.xpath("//*[contains(@class,'v-scrollable')]"));
        for (WebElement scrollable : scrollables) {
            assertNoHorizontalScrollbar(scrollable,
                    "Element should not have a horizontal scrollbar");
            assertNoVerticalScrollbar(scrollable,
                    "Element should not have a vertical scrollbar");
        }
    }

}
