package com.vaadin.tests.components.window;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowShouldRemoveActionHandlerTest extends MultiBrowserTest {

    @Test
    public void testRemovingActionHandlers() {
        openTestURL();

        addActionHandler();
        addAnotherActionHandler();

        assertState("An UI with 2 action handlers");
        addActionHandler();

        assertState("An UI with 3 action handlers");
        removeActionHandler();
        removeActionHandler();

        assertState("An UI with 3 action handlers - Removed handler - Removed handler");
        addActionHandler();

        assertState("An UI with 2 action handlers");
    }

    private void removeActionHandler() {
        $(ButtonElement.class).caption("Remove an action handler").first()
                .click();
    }

    private void addAnotherActionHandler() {
        $(ButtonElement.class).caption("Add another action handler").first()
                .click();
    }

    private void addActionHandler() {
        $(ButtonElement.class).caption("Add an action handler").first().click();
    }

    private void assertState(String expected) {
        Assert.assertEquals("Unexpected state,", expected,
                $(LabelElement.class).id("state").getText());
    }
}
