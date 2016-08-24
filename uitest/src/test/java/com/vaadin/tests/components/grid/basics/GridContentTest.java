package com.vaadin.tests.components.grid.basics;

import org.junit.Assert;
import org.junit.Test;

public class GridContentTest extends GridBasicsTest {

    @Test(expected = AssertionError.class)
    public void testHtmlRenderer() {
        DataObject first = getTestData().findFirst().orElse(null);
        Assert.assertEquals("Text content should match row number",
                first.getRowNumber().toString(),
                getGridElement().getCell(0, 2).getText());
        Assert.assertEquals("HTML content did not match", first.getHtmlString(),
                getGridElement().getCell(0, 2).getAttribute("innerHTML"));
    }
}
