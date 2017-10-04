package com.vaadin.tests.components.tabsheet;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class FirstTabNotVisibleInTabsheetTest extends MultiBrowserTest {
    @Test
    public void testFirstTabIsVisibleAfterBeingInvisible() {
        openTestURL();

        toggleFirstTabVisibility();
        toggleFirstTabVisibility();

        TabSheetElement tabSheet = $(TabSheetElement.class).first();

        assertTrue("TabSheet should have first tab visible",
                tabSheet.getTabCaptions().contains("first visible tab"));
    }

    private void toggleFirstTabVisibility() {
        $(ButtonElement.class).caption("Toggle first tab").first().click();
    }
}
