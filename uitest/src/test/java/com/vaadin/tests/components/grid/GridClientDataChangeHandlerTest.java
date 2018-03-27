package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class GridClientDataChangeHandlerTest extends SingleBrowserTest {

    @Test
    public void testNoErrorsOnGridInit() throws InterruptedException {
        setDebug(true);
        openTestURL();

        // Wait for delayed functionality.
        sleep(1000);

        assertFalse("Unexpected exception is visible.",
                $(NotificationElement.class).exists());
    }
}
