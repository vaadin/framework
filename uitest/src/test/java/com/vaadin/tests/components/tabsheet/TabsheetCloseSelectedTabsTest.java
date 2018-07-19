package com.vaadin.tests.components.tabsheet;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;

import java.io.IOException;

/**
 * Tests that tabsheet's scroll button are rendered correctly in Chameleon
 * theme.
 *
 * Ticket #12154
 *
 * @author Vaadin Ltd
 */
public class TabsheetCloseSelectedTabsTest extends SingleBrowserTest {

    @Test
    public void deleteSelectedTab() {
        openTestURL();
        $(ButtonElement.class).caption("Select last tab").first().click();
        assertElementPresent(By.className("v-tabsheet-tabitem"));
        $(TabSheetElement.class).first().waitForVaadin();
        $(ButtonElement.class).caption("Remove all tabs").first().click();
        assertElementNotPresent(By.className("v-tabsheet-tabitem"));
    }
}
