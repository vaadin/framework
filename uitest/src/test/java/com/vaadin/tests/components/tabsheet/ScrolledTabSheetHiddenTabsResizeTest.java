package com.vaadin.tests.components.tabsheet;

import java.util.List;

import org.openqa.selenium.WebElement;

public class ScrolledTabSheetHiddenTabsResizeTest
        extends ScrolledTabSheetResizeTest {

    @Override
    public void setup() throws Exception {
        lastVisibleTabCaption = "Tab 39";
        super.setup();
    }

    @Override
    protected WebElement getFirstHiddenViewable(List<WebElement> tabs) {
        // every other tab is hidden on server, return the second-to-last tab
        // before the first one that is visible on client
        WebElement previous = null;
        WebElement older = null;
        for (WebElement tab : tabs) {
            if (hasCssClass(tab, "v-tabsheet-tabitemcell-first")) {
                break;
            }
            older = previous;
            previous = tab;
        }
        return older;
    }
}
