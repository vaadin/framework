package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridEditorConverterNotFoundTest extends GridBasicFeaturesTest {

    @Override
    protected Class<?> getUIClass() {
        // Use the correct UI with helpers from GridBasicFeatures
        return GridEditorConverterNotFound.class;
    }

    @Test
    public void testConverterNotFound() {
        openTestURL();

        $(GridElement.class).first().getCell(0, 0).doubleClick();

        assertEquals("1. com.vaadin.data.Buffered$SourceException",
                getLogRow(0));
    }
}
