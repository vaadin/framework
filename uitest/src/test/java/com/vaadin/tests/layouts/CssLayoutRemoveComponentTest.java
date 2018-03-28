package com.vaadin.tests.layouts;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTestPhantomJS2;

public class CssLayoutRemoveComponentTest extends SingleBrowserTestPhantomJS2 {
    @Test
    public void testRemoveOnlyNecessaryComponentsFromDom() {
        openTestURL();

        String script = "document.mutationEventCount = 0;"
                + "var observer = new MutationObserver(function(mutations) {"
                + "mutations.forEach(function(mutation) { document.mutationEventCount += mutation.removedNodes.length; });"
                + "});"
                + "observer.observe(arguments[0].parentNode, { childList: true });";

        executeScript(script,
                $(TextFieldElement.class).caption("Caption1").first());

        $(ButtonElement.class).first().click();

        Long mutationEvents = (Long) executeScript(
                "return document.mutationEventCount;");
        assertEquals(
                "Parent should only have two mutation events (remove field and its caption)",
                2, mutationEvents.intValue());
    }
}
