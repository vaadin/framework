package com.vaadin.tests.applicationcontext;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class CleanupBrokenUITest extends SingleBrowserTest {

    @Test
    public void ensureUIDetached() {
        openTestURL();
        // UI 1 has not yet been added in UI.init where logging takes place
        assertEquals("1. UIs in session: 0", getLogRow(0));

        String url = getTestURL(getUIClass()).replace("restartApplication",
                "1");
        driver.get(url);
        // UI 1 remains in session during UI2 init where logging takes place
        assertEquals("1. UIs in session: 1", getLogRow(0));

        // At this point UI1 should be removed from the session
        driver.get(url);

        // UI 2 remains in session during UI3 init where logging takes place
        // UI 1 should have been removed
        assertEquals("1. UIs in session: 1", getLogRow(0));
    }
}
