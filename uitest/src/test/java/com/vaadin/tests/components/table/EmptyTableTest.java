package com.vaadin.tests.components.table;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class EmptyTableTest extends MultiBrowserTest {

    @Test
    public void test() {
        setDebug(true);
        openTestURL();

        ensureNoErrors();
    }

    private void ensureNoErrors() {
        if (isElementPresent(NotificationElement.class)) {
            Assert.fail("Error notification was shown!");
        }
    }

}
