package com.vaadin.tests.components.embedded;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FlashIsVisibleTest
        extends com.vaadin.tests.components.flash.FlashIsVisibleTest {

    @Override
    @Test
    public void testFlashIsCorrectlyDisplayed() throws Exception {
        assertTrue("Test is using wrong url",
                getTestUrl().contains(".embedded."));
        super.testFlashIsCorrectlyDisplayed();
    }
}
