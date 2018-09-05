package com.vaadin.tests.components.javascriptcomponent;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class JavaScriptStateTrackingTest extends SingleBrowserTest {
    @Test
    public void testStateTracking() {
        openTestURL();

        // field2 should really be null instead of undefined, but that's a
        // separate issue
        assertValues(0, "initial value", "undefined");

        $(ButtonElement.class).id("setField2").click();

        assertValues(1, "initial value", "updated value 1");

        $(ButtonElement.class).id("clearField1").click();

        assertValues(2, "null", "updated value 1");

        $(ButtonElement.class).id("setField2").click();

        assertValues(3, "null", "updated value 3");
    }

    private void assertValues(int expectedCounter, String expectedField1,
            String expectedField2) {
        assertEquals(String.valueOf(expectedCounter),
                findElement(By.id("counter")).getText());
        assertEquals(String.valueOf(expectedField1),
                findElement(By.id("field1")).getText());
        assertEquals(String.valueOf(expectedField2),
                findElement(By.id("field2")).getText());
    }
}
