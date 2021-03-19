package com.vaadin.v7.tests.components.grid.basicfeatures.server;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridColumnMaxWidthTest extends GridBasicFeaturesTest {

    @Test
    public void testMaxWidthAffectsColumnWidth() {
        setDebug(true);
        openTestURL();

        selectMenuPath("Component", "Columns",
                "All columns expanding, Col 0 has max width of 40px");

        assertEquals("Column 0 did not obey max width of 40px.", 40,
                getGridElement().getCell(0, 0).getSize().getWidth());
    }
}
