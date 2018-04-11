package com.vaadin.tests.components.gridlayout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutCellSizesUITest extends MultiBrowserTest {

    private List<WebElement> slots4x4;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        slots4x4 = getSlots();
    }

    @Test
    public void equalsHeightSlotsShouldHaveTheSameHeight() {
        // items in positions 0,1,2,3,4,5 should have the same height
        int firstSlotHeight = getSlotHeight(0);
        for (int i = 1; i < 6; i++) {
            assertEquals("Cell height didn't match for cell: " + i,
                    firstSlotHeight, getSlotHeight(i));
        }
    }

    @Test
    public void expandedAndLargeSizeSlotsShouldNotEqualFirstSlot() {
        int firstSlotHeight = getSlotHeight(0);

        assertNotMatchesSmallHeight(firstSlotHeight, 6, "Row 2");
        assertNotMatchesSmallHeight(firstSlotHeight, 7, "1x2 cell");
        assertNotMatchesSmallHeight(firstSlotHeight, 8, "A 2x2 date field");
        assertNotMatchesSmallHeight(firstSlotHeight, 9, "Row 3");
    }

    @Test
    public void expandedRowsShouldHaveCorrectHeight() {
        // Slots expanding over 2 rows should have the same height.
        assertEquals("1x2 and 2x2 cell heights didn't match", getSlotHeight(7),
                getSlotHeight(8));

        // Slots on same row as the 1x2 label should have the same combined
        // height.
        assertEquals(
                "1x2 and combined row two and row three cell heights didn't match",
                getSlotHeight(7), getSlotHeight(6) + getSlotHeight(9));
    }

    @Test
    public void expandedRowsShouldHaveCorrectWidth() {
        // Col 2 slot should be the dame width as 1x2 cell slot
        assertEquals("Col 2 slot was not the same width as slot for 1x2 cell",
                getSlotWidth(1), getSlotWidth(7));

        // Row one col 3 & 4 should be as wide as the 2x2 date field
        assertEquals(
                "2x2 date field width didn't match col 3 & col 4 combined width",
                getSlotWidth(8), getSlotWidth(2) + getSlotWidth(3));

        // 3x1 button should be as wide as 1x2cell + 2x2 data field
        assertEquals(
                "3x1 slot width wasn't the same as the combined slot widths of 1x2 cell and 2x2 date field",
                getSlotWidth(5), getSlotWidth(7) + getSlotWidth(8));

    }

    private void assertNotMatchesSmallHeight(int firstSlotHeight, int i,
            String id) {
        assertNotEquals("Big slot '" + id + "' matched small slots in height",
                firstSlotHeight, getSlotHeight(i));
    }

    private int getSlotHeight(int slot) {
        return slots4x4.get(slot).getSize().height;
    }

    private int getSlotWidth(int slot) {
        return slots4x4.get(slot).getSize().width;
    }

    private List<WebElement> getSlots() {
        GridLayoutElement layout = $(GridLayoutElement.class).first();

        return layout.findElements(By.className("v-gridlayout-slot"));
    }
}
