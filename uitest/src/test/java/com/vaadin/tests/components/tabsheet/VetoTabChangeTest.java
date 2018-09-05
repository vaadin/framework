package com.vaadin.tests.components.tabsheet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class VetoTabChangeTest extends SingleBrowserTest {
    @Test
    public void testReselectTabAfterVeto() {
        openTestURL();

        TabSheetElement tabSheet = $(TabSheetElement.class).first();
        assertEquals("Tab 1 should be there by default", "Tab 1",
                getTabContent(tabSheet));

        tabSheet.openTab(1);

        assertEquals("Tab should not have changed", "Tab 1",
                getTabContent(tabSheet));

        tabSheet.openTab(0);
        assertEquals("Tab should still be there", "Tab 1",
                getTabContent(tabSheet));
    }

    private String getTabContent(TabSheetElement tabSheet) {
        return tabSheet.getContent(LabelElement.class).getText();
    }
}
