package com.vaadin.tests.components.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIInitExceptionTest extends MultiBrowserTest {
    @Test
    public void testExceptionOnUIInit() throws Exception {
        openTestURL();
        Assert.assertTrue("Page does not contain the given text", driver
                .getPageSource().contains("Catch me if you can"));
    }
}
