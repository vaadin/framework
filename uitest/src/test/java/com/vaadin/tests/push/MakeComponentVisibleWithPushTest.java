package com.vaadin.tests.push;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class MakeComponentVisibleWithPushTest extends SingleBrowserTest {

    @Test
    public void showingHiddenComponentByPushWorks() {
        setDebug(true);
        openTestURL();

        $(ButtonElement.class).first().click();
        assertEquals("Unexpected row count", 1,
                $(GridElement.class).first().getRowCount());
        $(ButtonElement.class).first().click();
        assertEquals("Unexpected row count", 2,
                $(GridElement.class).first().getRowCount());

        assertNoErrorNotifications();
    }
}
