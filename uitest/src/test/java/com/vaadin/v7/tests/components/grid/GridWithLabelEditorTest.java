package com.vaadin.v7.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridWithLabelEditorTest extends SingleBrowserTest {

    @Test
    public void testNoExceptionOnEdit() {
        setDebug(true);
        openTestURL();

        assertNoErrorNotifications();

        assertEquals("LabelEditor content not correct.", "FooFoo",
                $(GridElement.class).first().getEditor().getField(0).getText());
    }
}
