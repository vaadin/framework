package com.vaadin.tests.components.grid;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridSizeChangeTest extends MultiBrowserTest {

    private TabSheetElement tabSheet;
    private GridElement grid;
    private WebElement vScrollbar;
    private WebElement hScrollbar;

    @Test
    public void scrollbarsTakenIntoAccountInSizeChanges() {
        openTestURL();
        tabSheet = $(TabSheetElement.class).first();
        grid = $(GridElement.class).first();

        vScrollbar = grid.findElement(By.className("v-grid-scroller-vertical"));
        hScrollbar = grid
                .findElement(By.className("v-grid-scroller-horizontal"));

        // ensure no initial scrollbars
        ensureVerticalScrollbar(false);
        ensureHorizontalScrollbar(false);

        assertGridWithinTabSheet();

        $(ButtonElement.class).caption("Reduce height").first().click();
        // more rows than height -> scrollbar

        assertGridWithinTabSheet();
        ensureVerticalScrollbar(true);
        ensureHorizontalScrollbar(false);

        $(ButtonElement.class).caption("Remove row").first().click();
        // height matches rows -> no scrollbar

        assertGridWithinTabSheet();
        ensureVerticalScrollbar(false);
        ensureHorizontalScrollbar(false);

        $(ButtonElement.class).caption("Reduce width").first().click();
        // column too wide -> scrollbar

        assertGridWithinTabSheet();
        ensureVerticalScrollbar(false);
        ensureHorizontalScrollbar(true);

        $(ButtonElement.class).caption("Increase width").first().click();
        // column fits -> no scrollbar

        assertGridWithinTabSheet();
        ensureVerticalScrollbar(false);
        ensureHorizontalScrollbar(false);

        $(ButtonElement.class).caption("Add row").first().click();
        // more rows than height -> scrollbar

        assertGridWithinTabSheet();
        ensureVerticalScrollbar(true);
        ensureHorizontalScrollbar(false);

        $(ButtonElement.class).caption("Increase height").first().click();
        // height matches rows -> no scrollbar

        assertGridWithinTabSheet();
        ensureVerticalScrollbar(false);
        ensureHorizontalScrollbar(false);
    }

    private void ensureVerticalScrollbar(boolean displayed) {
        assertEquals(displayed ? "block" : "none",
                vScrollbar.getCssValue("display"));
    }

    private void ensureHorizontalScrollbar(boolean displayed) {
        assertEquals(displayed ? "block" : "none",
                hScrollbar.getCssValue("display"));
    }

    private void assertGridWithinTabSheet() throws AssertionError {
        // allow two pixel leeway
        assertThat(
                "Grid and TabSheet should always have the same bottom position, "
                        + "not be offset by a scrollbar's thickness",
                (double) grid.getLocation().getY() + grid.getSize().getHeight(),
                closeTo(tabSheet.getLocation().getY()
                        + tabSheet.getSize().getHeight(), 2));
    }
}
