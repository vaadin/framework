package com.vaadin.tests.application;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class RefreshStatePreserveTitleTest extends MultiBrowserTest {
    @Test
    public void testReloadingPageDoesNotResetTitle() throws Exception {
        openTestURL();
        assertTitleText();
        openTestURL();
        assertTitleText();
    }

    private void assertTitleText() {
        assertEquals("Incorrect page title,", "TEST", driver.getTitle());
    }
}
