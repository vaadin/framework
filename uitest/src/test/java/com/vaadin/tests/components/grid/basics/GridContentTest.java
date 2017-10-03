package com.vaadin.tests.components.grid.basics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GridContentTest extends GridBasicsTest {

    @Test
    public void testHtmlRenderer() {
        DataObject first = getTestData().findFirst().orElse(null);
        assertEquals("Text content should match row number",
                first.getRowNumber().toString(),
                getGridElement().getCell(0, 5).getText());
        assertEquals("HTML content did not match", first.getHtmlString(),
                getGridElement().getCell(0, 5).getAttribute("innerHTML"));
    }
}
