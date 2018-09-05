package com.vaadin.tests.layouts;

import static org.junit.Assert.assertEquals;

import org.junit.Assume;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class CssLayoutRemoveComponentTest extends SingleBrowserTest {
    @Test
    public void testRemoveOnlyNecessaryComponentsFromDom() {
        Assume.assumeFalse("PhantomJS has issues with this test",
                BrowserUtil.isPhantomJS(getDesiredCapabilities()));

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
