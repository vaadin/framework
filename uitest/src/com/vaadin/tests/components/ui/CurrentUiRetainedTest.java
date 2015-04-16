package com.vaadin.tests.components.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CurrentUiRetainedTest extends MultiBrowserTest {
    @Test
    public void testCurrentUiRetained() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertLogText(3, "1. Correct UI.getCurrent before GC: true");
        assertLogText(2, "2. Correct UI.getCurrent after GC: true");
        assertLogText(1, "3. GC probe available before GC: true");
        assertLogText(0, "4. GC probe available after GC: false");
    }

    private void assertLogText(int index, String expected) {
        Assert.assertEquals("Unexpected log contents,", expected,
                getLogRow(index));
    }
}
