package com.vaadin.tests.fieldgroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.AbstractHasTestBenchCommandExecutor;
import com.vaadin.testbench.elements.AbstractComponentElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTestPhantomJS2;

public class BasicCrudGridTest extends SingleBrowserTestPhantomJS2 {

    @Test
    public void fieldsInitiallyEmpty() {
        openTestURL();
        List<TextFieldElement> textFields = getFieldsLayout()
                .$(TextFieldElement.class).all();

        for (TextFieldElement e : textFields) {
            assertEquals("TextField should be empty", "", e.getValue());
        }
    }

    private AbstractHasTestBenchCommandExecutor getFieldsLayout() {
        return $(AbstractComponentElement.class).id("form");
    }

    @Test
    public void fieldsClearedOnDeselect() {
        openTestURL();

        // Select row
        $(GridElement.class).first().getCell(2, 2).click();

        List<TextFieldElement> textFields = getFieldsLayout()
                .$(TextFieldElement.class).all();

        for (TextFieldElement e : textFields) {
            assertNotEquals("TextField should not be empty", "", e.getValue());
        }

        // Deselect row
        $(GridElement.class).first().getCell(2, 2).click();

        for (TextFieldElement e : textFields) {
            assertEquals("TextField should be empty", "", e.getValue());
        }

    }
}
