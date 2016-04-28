package com.vaadin.tests.application;

import org.junit.Assert;
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
        Assert.assertEquals("Incorrect page title,", "TEST", driver.getTitle());
    }
}
