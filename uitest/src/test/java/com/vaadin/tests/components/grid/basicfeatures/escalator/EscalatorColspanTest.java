package com.vaadin.tests.components.grid.basicfeatures.escalator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;

public class EscalatorColspanTest extends EscalatorBasicClientFeaturesTest {
    private static final int NO_COLSPAN = 1;

    @Test
    public void testNoColspan() {
        openTestURL();
        populate();

        assertEquals(NO_COLSPAN, getColSpan(getHeaderCell(0, 0)));
        assertEquals(NO_COLSPAN, getColSpan(getBodyCell(0, 0)));
        assertEquals(NO_COLSPAN, getColSpan(getFooterCell(0, 0)));
    }

    @Test
    public void testColspan() {
        openTestURL();
        populate();

        int firstCellWidth = getBodyCell(0, 0).getSize().getWidth();
        int secondCellWidth = getBodyCell(0, 1).getSize().getWidth();
        int doubleCellWidth = firstCellWidth + secondCellWidth;

        selectMenuPath(FEATURES, COLUMN_SPANNING, COLSPAN_NORMAL);

        WebElement bodyCell = getBodyCell(0, 0);
        assertEquals("Cell was not spanned correctly", 2, getColSpan(bodyCell));
        assertEquals(
                "Spanned cell's width was not the sum of the previous cells ("
                        + firstCellWidth + " + " + secondCellWidth + ")",
                doubleCellWidth, bodyCell.getSize().getWidth(), 1);
    }

    @Test
    public void testColspanToggle() {
        openTestURL();
        populate();

        int singleCellWidth = getBodyCell(0, 0).getSize().getWidth();

        selectMenuPath(FEATURES, COLUMN_SPANNING, COLSPAN_NORMAL);
        selectMenuPath(FEATURES, COLUMN_SPANNING, COLSPAN_NONE);

        WebElement bodyCell = getBodyCell(0, 0);
        assertEquals(NO_COLSPAN, getColSpan(bodyCell));
        assertEquals(singleCellWidth, bodyCell.getSize().getWidth(), 1);
    }

    private static int getColSpan(WebElement cell) {
        String attribute = cell.getAttribute("colspan");
        if (attribute == null) {
            return NO_COLSPAN;
        } else {
            return Integer.parseInt(attribute);
        }
    }
}
