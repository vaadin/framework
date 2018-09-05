package com.vaadin.tests.components.treetable;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for absence of empty row header for RowHeaderMode.ICON_ONLY
 *
 * @author Vaadin Ltd
 */
public class TreeTableRowHeaderModeTest extends MultiBrowserTest {

    @Test
    public void testIconRowHeaderMode() {
        openTestURL();

        assertFalse("Unexpected row header for icons is found in TreeTable",
                isElementPresent(
                        By.className("v-table-header-cell-rowheader")));
    }
}
