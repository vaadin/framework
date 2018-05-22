package com.vaadin.tests.components.table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableRemovedQuicklySendsInvalidRpcCallsTest
        extends MultiBrowserTest {

    private static final String BUTTON_ID = TableRemovedQuicklySendsInvalidRpcCalls.BUTTON_ID;
    private static final String FAILURE_CAPTION = TableRemovedQuicklySendsInvalidRpcCalls.FAILURE_CAPTION;
    private static final String SUCCESS_CAPTION = TableRemovedQuicklySendsInvalidRpcCalls.SUCCESS_CAPTION;

    @Test
    public void test() throws Exception {
        setDebug(true);
        openTestURL();

        assertFalse("Test started with the error present.",
                button().getText().equals(FAILURE_CAPTION));
        assertFalse("Test jumped the gun.",
                button().getText().equals(SUCCESS_CAPTION));

        button().click();
        Thread.sleep(5000);

        assertFalse("Test failed after trying to trigger the error.",
                button().getText().equals(FAILURE_CAPTION));
        assertTrue("Test didn't end up in correct success state.",
                button().getText().equals(SUCCESS_CAPTION));
    }

    private WebElement button() {
        return vaadinElementById(BUTTON_ID);
    }
}
