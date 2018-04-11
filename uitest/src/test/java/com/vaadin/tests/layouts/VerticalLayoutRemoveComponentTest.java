package com.vaadin.tests.layouts;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class VerticalLayoutRemoveComponentTest
        extends SingleBrowserTest {
    @Test
    public void testRemoveOnlyNecessaryComponentsFromDom() {
        openTestURL();

        String script = "document.mutationEventCount = 0;"
                + "var observer = new MutationObserver(function(mutations) {"
                + "mutations.forEach(function(mutation) { document.mutationEventCount += mutation.removedNodes.length; });"
                + "});"
                + "observer.observe(arguments[0], { childList: true });";

        executeScript(script,
                $(VerticalLayoutElement.class).id("targetLayout"));

        $(ButtonElement.class).first().click();

        Long mutationEvents = (Long) executeScript(
                "return document.mutationEventCount;");
        assertEquals("Parent should only have one mutation event (remove slot)",
                1, mutationEvents.intValue());
    }
}
