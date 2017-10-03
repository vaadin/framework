package com.vaadin.tests.applicationcontext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class RpcForClosedUITest extends MultiBrowserTest {
    @Override
    protected Class<?> getUIClass() {
        return CloseUI.class;
    }

    @Test
    public void testRpcForUIClosedInBackground() throws Exception {
        openTestURL();
        /* Close the UI in a background thread */
        clickButton("Close UI (background)");
        /* Try to log 'hello' */
        clickButton("Log 'hello'");
        /* Ensure 'hello' was not logged */
        checkLogMatches("2. Current WrappedSession id: .*");
        assertFalse("Page contains word 'Hello'",
                driver.getPageSource().contains("Hello"));
    }

    private void clickButton(String caption) {
        $(ButtonElement.class).caption(caption).first().click();
    }

    private void checkLogMatches(String expected) {
        String actual = getLogRow(0);
        assertTrue(String.format(
                "Unexpected log row.\n expected format: '%s'\n was: '%s'",
                expected, actual), actual.matches(expected));
    }
}
